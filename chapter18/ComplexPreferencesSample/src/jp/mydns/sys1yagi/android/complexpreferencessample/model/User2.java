/**
 * 
 */
package jp.mydns.sys1yagi.android.complexpreferencessample.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * @author yagitoshihiro
 *
 */
public class User2 implements Serializable{
    
    @SerializedName("id")
    private String mId;
    
    
    @SerializedName("token")
    private AccessToken mToken;
    
    public User2() {

    }

    @Override
    public String toString() {
        return "User2 [mId=" + mId + ", mToken=" + mToken + "]";
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }


    public AccessToken getToken() {
        return mToken;
    }

    public void setToken(AccessToken token) {
        mToken = token;
    }
    
    static public class AccessToken implements Serializable{

        @SerializedName("access_token")
        private String mAccessToken;
        
        public AccessToken() {

        }
        public String getAccessToken() {
            return mAccessToken;
        }
        public void setAccessToken(String accessToken) {
            mAccessToken = accessToken;
        }
        @Override
        public String toString() {
            return "AccessToken [mAccessToken=" + mAccessToken + "]";
        }
    }
}
