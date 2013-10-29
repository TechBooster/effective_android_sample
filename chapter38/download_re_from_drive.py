#!/usr/bin/python
# -*- coding: utf-8 -*-
#
#   Copyright [2013] [amedama (@amedama)]
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#
'''
C84 Techbooster 『Effective Android』 第21章用 サンプル

原稿版と使っているAPIは同じですが、構成が変わっている点、ご容赦ください。
大体以下のような違いがあります (他にもあるかもしれません)。

 - インデントをPython標準の4 spacesに変更。
 - 注釈を追加。
 - 公開するべきでない情報 (ID/SECRET) を別ファイルに移動。
  - 読んでいる方がこのスクリプトを試す場合は、別途このファイルがある
    ディレクトリにsecret.py を用意するか、このコードを書き換えてください。
 - エラー対処用のコードを余計に追加。

前準備としてGoogle Drive内のフォルダも作らないといけません。

 Licensed under the Apache License, Version 2.0 (the "License").
'''

import httplib2
import pprint
import sys

from apiclient.discovery import build
from oauth2client.client import OAuth2WebServerFlow
from oauth2client.file import Storage

# 公開出来ない情報については secret.py に保存します
# OAuth 2.0 の Client ID と Client Secret を
# CLIENT_ID, CLIENT_SECRET という形で保存します。
try:
    import secret
except ImportError:
    print >>sys.stderr, u'''\

Google API Console (https://code.google.com/apis/console/) から
OAuth 2.0 の Client ID/Secret を取得し、
secret.py というファイルに保存してください。

例 (値は仮のものです):
--
CLIENT_ID = '653537439671.appsapps.googleusercontent.com'
CLIENT_SECRET = '94WwQ10pAR26_48rn-uoOIbh'
--
'''
    sys.exit(1)

CLIENT_ID = secret.CLIENT_ID
CLIENT_SECRET = secret.CLIENT_SECRET

# 'https://www.googleapis.com/auth/drive.readonly'
# "Allows read-only access to file metadata and file content"
#
# See also: https://developers.google.com/drive/scopes
OAUTH_SCOPE = 'https://www.googleapis.com/auth/drive.readonly'

# デスクトップアプリケーションはリダイレクト先にこのURNを使います。
REDIRECT_URI = 'urn:ietf:wg:oauth:2.0:oob'

if __name__ == '__main__':
    storage = Storage('credential_storage')
    credentials = storage.get()
    if not credentials:
        flow = OAuth2WebServerFlow(CLIENT_ID, CLIENT_SECRET, OAUTH_SCOPE,
                                   REDIRECT_URI)
        authorize_url = flow.step1_get_authorize_url()
        print 'Go to the following link in your browser: ' + authorize_url
        code = raw_input('Enter verification code: ').strip()
        credentials = flow.step2_exchange(code)
        storage.put(credentials)

    http = httplib2.Http()
    http = credentials.authorize(http)
    service = build('drive', 'v2', http=http)

    # "c84_techbooster" という名前のフォルダを調べます。
    param = {'q': (u"title = '{}' and mimeType = '{}'"
                   .format('c84_techbooster',
                           'application/vnd.google-apps.folder'))}
    root_items  = service.files().list(**param).execute()['items']
    ''' こうやっても必要な情報は取れます。

    param = {'q': (u"title = '{}' and mimeType = '{}'"
                   .format('c84_techbooster',
                           'application/vnd.google-apps.folder'))}
    root_items  = service.children().list(folderId='root',
                                          **param).execute()['items']
    '''

    if len(root_items) == 0:
        print >>sys.stderr, u'''
このサンプルコードは、Google Drive 上に以下のようなフォルダ構成があることを
仮定しています:

 c84_techbooster/
   - (author名)/(author名).re
   - (author名)/(author名).re
   - ..

とりあえず c84_techbooster/ が見つかりませんでした。
'''
        sys.exit(1)


    assert len(root_items) == 1, 'len(items): {}'.format(len(root_items))
    pprint.pprint(root_items)

    root_id = root_items[0][u'id']
    param = {'q': (u"'{}' in parents and mimeType = '{}'"
                   .format(root_id, 'application/vnd.google-apps.folder'))}
    author_dir_items = service.files().list(**param).execute()['items']
    for author_dir_item in author_dir_items:
        author_name = author_dir_item[u'title']
        author_dir_id = author_dir_item[u'id']
        filename = u'{}.re'.format(author_name)
        param = {'q': ((u'"{}" in parents'
                        u' and title = "{}"'
                        u' and trashed = false')
                       .format(author_dir_id, filename))}
        author_items = service.files().list(**param).execute()[u'items']
        # ここ、原稿の内容とスクリプトに齟齬がありました。
        # 謹んでお詫び申し上げます。
        #
        # オリジナルのままだと
        #   author_name/author_name.re
        # というファイルがなかった場合にエラーで終了してしまいます。
        if len(author_items) == 0:
            continue

        # ここは原稿で言及している通りです。
        # サンプルでは対処コード例も載せちゃう
        if len(author_items) > 1:
            def select_latest_item(items):
                latest_item = None
                for item in items:
                    if (not latest_item or
                        latest_item[u'modifiedDate'] < item[u'modifiedDate']):
                        latest_item = item
                return latest_item
            author_item = select_latest_item(author_items)
        else:
            author_item = author_items[0]

        pprint.pprint(author_item)
        resp, content = service._http.request(author_item[u'downloadUrl'])

        # ここも原稿の内容だと若干良くないケースがありましたので少し修正。
        # - 読み取り権限がない場合に 403 が返ってくるのはいたって正常ですね。
        #
        # なお、原稿執筆時点では、200 を返すべき正常なリクエストに対しても
        # サーバが 200 以外を返すケースが稀にありました。
        # これについても、私が埋め込んだバグの可能性がありますけどネ
        if (resp.status == 403): # Permission Denied
            '404 for file "{}". Ignoring it.'.format(author_name)
            continue
        # 予想外の結果はなるべく assert で落としてます。
        assert resp.status == 200,\
            ('Response Status was not 200 ({}). File: "{}"'
             .format(resp.status, author_name))

        f = file(filename, 'w')
        f.write(content)
        f.close()
