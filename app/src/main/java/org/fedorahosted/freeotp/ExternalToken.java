/*
 * FreeOTP
 *
 * Authors: Petter Arvidsson <petter.arvidsson@fidesmo.com>
 *
 * Copyright (C) 2014 Petter Arvidsson, Fidesmo AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp;

import java.io.IOException;
import android.net.Uri;
import com.fidesmo.oath.hardware.HardwareToken;
import com.fidesmo.oath.hardware.TokenMeta;

public class ExternalToken implements Token {
    private String issuer;
    private String label;
    private String id;
    private TokenType type;
    private int digits;
    private String algorithm;
    private HardwareToken token;
    private int period = 30;

    public ExternalToken(TokenMeta meta, HardwareToken token) {
        this.id = meta.getLabel();
        this.token = token;
        String s[] = meta.getLabel().split(":", 2);
        if(s.length == 1) {
            label = s[0];
        } else {
            issuer = s[0];
            label = s[1];
        }

        switch(meta.getType()) {
        case TOTP:
            this.type = TokenType.TOTP;
            break;
        case HOTP:
            this.type = TokenType.HOTP;
            break;
        }
        switch(meta.getAlgorithm()) {
        case SHA1:
            algorithm = "SHA1";
            break;
        case SHA256:
            algorithm = "SHA256";
            break;
        }
        this.digits = meta.getDigits();
    }

    public String getID() {
        return id;
    }

    public String getIssuer() {
        return issuer != null ? issuer : "";
    }

    public void setIssuer(String issuer) {
        throw new UnsupportedOperationException();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        throw new UnsupportedOperationException();
    }

    public int getDigits() {
        return digits;
    }

    private TokenCode generateTotpCode(long counter, TokenCode next) throws IOException {
        return new TokenCode(token.readTotpCode(id, counter), counter * period * 1000, (counter + 1) * period * 1000, next);
    }

    private TokenCode generateHotpCode(long cur) throws IOException {
        return new TokenCode(token.readHotpCode(id), cur, cur + (period * 1000));
    }

    public TokenCode generateCodes() throws IOException {
        try {
            token.open();
            long cur = System.currentTimeMillis();
            switch(type) {
            case TOTP:
                long counter = cur / 1000 / period;
                return generateTotpCode(counter, generateTotpCode(counter + 1, null));
            case HOTP:
                return generateHotpCode(cur);
            default:
                throw new IOException("Invalid token type");
            }
        } finally {
            token.close();
        }
    }

    public TokenType getType() {
        return type;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Uri toUri() {
        return null;
    }

    public Uri getImage() {
        return null;
    }

    public void setImage(Uri image) {
        throw new UnsupportedOperationException();
    }

    public boolean isInternal() {
        return false;
    }
}
