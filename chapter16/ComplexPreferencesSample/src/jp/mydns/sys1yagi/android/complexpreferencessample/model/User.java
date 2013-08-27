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
public class User implements Serializable{
    
    @SerializedName("name")
    private String mName;
    
    @SerializedName("id")
    private String mId;
    
    @SerializedName("profile")
    private String mProfile;
    
    @SerializedName("token")
    private AccessToken mToken;
    
    public User() {

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getProfile() {
        return mProfile;
    }

    public void setProfile(String profile) {
        mProfile = profile;
    }

    public AccessToken getToken() {
        return mToken;
    }

    public void setToken(AccessToken token) {
        mToken = token;
    }
    
    
    @Override
    public String toString() {
        return "User [mName=" + mName + ", mId=" + mId + ", mProfile=" + mProfile + ", mToken=" + mToken + "]";
    }

    static public class AccessToken implements Serializable{

        @SerializedName("access_token")
        private String mAccessToken;
        
        @SerializedName("access_token_secret")
        private String mAccessTokenSecret;
        public AccessToken() {

        }
        public String getAccessToken() {
            return mAccessToken;
        }
        public void setAccessToken(String accessToken) {
            mAccessToken = accessToken;
        }
        public String getAccessTokenSecret() {
            return mAccessTokenSecret;
        }
        public void setAccessTokenSecret(String accessTokenSecret) {
            mAccessTokenSecret = accessTokenSecret;
        }
        @Override
        public String toString() {
            return "AccessToken [mAccessToken=" + mAccessToken + ", mAccessTokenSecret=" + mAccessTokenSecret + "]";
        }
        
    }
}
