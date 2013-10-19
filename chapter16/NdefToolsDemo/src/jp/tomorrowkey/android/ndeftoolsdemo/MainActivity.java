
package jp.tomorrowkey.android.ndeftoolsdemo;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.ndeftools.Message;
import org.ndeftools.util.activity.NfcTagWriterActivity;
import org.ndeftools.wellknown.Action;
import org.ndeftools.wellknown.ActionRecord;
import org.ndeftools.wellknown.SmartPosterRecord;
import org.ndeftools.wellknown.TextRecord;
import org.ndeftools.wellknown.UriRecord;

public class MainActivity extends NfcTagWriterActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // NFCの検出を有効にします
    setDetecting(true);
  }

  /**
   * NFC機能がない場合に呼ばれます
   */
  @Override
  protected void onNfcFeatureNotFound() {
    Toast.makeText(this, "お使いの端末はNFCに対応していません", Toast.LENGTH_SHORT).show();
    finish();
  }

  /**
   * NFC機能はあるが、有効になっていない場合に呼ばれます<br>
   * 設定アプリにてNFCを有効にすればNFCが使えます
   */
  @Override
  protected void onNfcStateDisabled() {
    Toast.makeText(this, "NFCが有効になっていません\n設定アプリでNFCを有効にしてください", Toast.LENGTH_SHORT).show();

    Intent intent = new Intent("android.settings.NFC_SETTINGS");
    startActivity(intent);
  }

  /**
   * NFCが使える状態の際に呼ばれます
   */
  @Override
  protected void onNfcStateEnabled() {
    Toast.makeText(this, "NFCが使えます", Toast.LENGTH_SHORT).show();
  }

  /**
   * NFC機能の状態が変化した場合に呼ばれます<br>
   * (ex. NFC OFF -> ON, ON -> OFF)
   * 
   * @param enabled
   */
  @Override
  protected void onNfcStateChange(boolean enabled) {
    String message = String.format("NFCが%sになりました", enabled ? "有効" : "無効");
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  /**
   * 書き込むNDEFメッセージを作成するためのメソッドです<br>
   * 書き込み直前に呼ばれます
   */
  @Override
  protected NdefMessage createNdefMessage() {
    // NDEFレコードの作成
    TextRecord title = new TextRecord("Android");
    UriRecord uri = new UriRecord("http://www.android.com/");
    ActionRecord action = new ActionRecord(Action.DEFAULT_ACTION);
    SmartPosterRecord smartPosterRecord = new SmartPosterRecord(title, uri, action);

    // NDEFメッセージの作成
    Message message = new Message();
    message.add(smartPosterRecord);

    return message.getNdefMessage();
  }

  /**
   * NDEFに対応していないNFCタグを検出した際に呼ばれます
   */
  @Override
  protected void writeNdefCannotWriteTech() {
    Toast.makeText(this, "このNFCタグはNDEFに対応していません。\n違うNFCタグをかざしてください。", Toast.LENGTH_SHORT)
        .show();
  }

  /**
   * NFCタグが書き込み禁止になっている場合に呼ばれます
   */
  @Override
  protected void writeNdefNotWritable() {
    Toast.makeText(this, "このNFCタグは書き込み禁止になっています。\n違うNFCタグをかざしてください。", Toast.LENGTH_SHORT)
        .show();
  }

  /**
   * 書き込もうとしたNDEFメッセージのサイズが大きすぎた場合に呼ばれます
   * 
   * @param required 書き込もうとしたNDEFメッセージのサイズ
   * @param capacity 検出されたNFCタグに書き込み可能なサイズ
   */
  @Override
  protected void writeNdefTooSmall(int required, int capacity) {
    Toast.makeText(this, "NFCタグのメモリが小さすぎて書き込めません。\n違うNFCタグをかざしてください。", Toast.LENGTH_SHORT)
        .show();
  }

  /**
   * NDEFの書き込みに成功した際に呼ばれます
   */
  @Override
  protected void writeNdefSuccess() {
    Toast.makeText(this, "書き込みに成功しました", Toast.LENGTH_SHORT).show();
  }

  /**
   * 書き込みに失敗した際に呼ばれます
   */
  @Override
  protected void writeNdefFailed(Exception e) {
    Log.d("Ndef tools for Android demo", "An exception has been occured", e);
    Toast.makeText(this, "書き込みに失敗しました。\nもう一度NFCタグをかざしてください。", Toast.LENGTH_SHORT).show();
  }

}
