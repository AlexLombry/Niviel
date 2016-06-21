package com.adrastel.niviel.assets;

public class Constants {

    public interface EXTRAS {
        String AUTHORIZATION_CODE = "authorization_code";
    }

    public interface PREFERENCES {
        String ACCESS_TOKEN = "access_token";
        String TOKEN_TYPE = "token_type";
        String EXPIRES_IN = "expires_in";
        String SCOPE = "scope";
        String CREATED_AT = "created_at";
        String RECORDS = "records";
    }

    public interface API {
        String GRANT_TYPE = "authorization_code";
        String ACCESS_TOKEN = "https://www.worldcubeassociation.org/oauth/token";
        String ME = "https://www.worldcubeassociation.org/api/v0/me";
        String REDIRECT_URI = "http://127.0.0.1/niviel/";
    }

    public interface CODES {
        int BASIC_REQUEST = 0x00;
    }

    public interface SECRETS {

        // note: (Herel Adrastel in SHA-1)
        String SHARED_PREFERENCES = "34e6f3b80f73857fde0b31d8acedcc8a5bb84a48";

        String CLIENT_ID = "8858b7016129d9494c165449e7454e38c97ad7d36dcd3679076243e7392d1f5e";
        String CLIENT_SECRET = "6c0cd96030f82a936125c6acc46347ac6c789b25afa2a101741e38b092707b79";
    }

}
