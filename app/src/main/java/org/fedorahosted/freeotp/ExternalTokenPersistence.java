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

import java.util.*;
import java.io.IOException;
import com.fidesmo.oath.hardware.*;
import com.yubico.yubioath.model.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.nfc.tech.IsoDep;

public class ExternalTokenPersistence implements TokenPersistence {
    HardwareToken store;
    byte[] id;
    List<TokenMeta> tokens;

    public ExternalTokenPersistence(IsoDep card) throws IOException{
        store = new YkneoOath(card);
        long timestamp = (System.currentTimeMillis() / 1000 + 10) / 30;
        store.open();
        tokens = store.getTokens(timestamp);
        store.close();
    }

    public Token addWithToast(Context ctx, String uri) {
        try {
            InternalToken token = new InternalToken(uri);
            add(token);
            return token;
        } catch (Token.TokenUriInvalidException e) {
            Toast.makeText(ctx, R.string.invalid_token, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(ctx, R.string.failed_to_read_external_token, Toast.LENGTH_SHORT).show();
        }
        return null;

    }

    public int length() {
        return tokens.size();
    }

    public Token get(int position) {
        return new ExternalToken(tokens.get(position), store);
    }

    private static TokenMeta tokenMeta(InternalToken token) {
        TokenMeta.Type type = null;
        switch(token.getType()) {
        case HOTP:
            type = TokenMeta.Type.HOTP;
            break;
        case TOTP:
            type = TokenMeta.Type.TOTP;
            break;
        }

        if(token.getAlgorithm().equals("SHA1")) {
            return new TokenMeta(token.getID(), token.getDigits(), type, TokenMeta.Algorithm.SHA1);
        } else if(token.getAlgorithm().equals("SHA256")) {
            return new TokenMeta(token.getID(), token.getDigits(), type, TokenMeta.Algorithm.SHA256);
        } else {
            return null;
        }
    }

    public void add(InternalToken token) throws Token.TokenUriInvalidException, IOException {
        try {
            store.open();
            if(token.getType() == Token.TokenType.HOTP)
                store.storeCode(tokenMeta(token), token.getSecret(), (int)token.getCounter());
            else
                store.storeCode(tokenMeta(token), token.getSecret(), token.getPeriod());
            long timestamp = (System.currentTimeMillis() / 1000 + 10) / 30;
        } finally {
            store.close();
        }
    }

    public void move(int fromPosition, int toPosition) {
        throw new UnsupportedOperationException();
    }

    public void delete(int position) throws IOException {
        try {
            Token token = new ExternalToken(tokens.get(position), store);
            store.open();
            store.deleteCode(token.getID());
        } finally {
            store.close();
        }
    }

    public void save(Token token) {
        /* Token in the external hardware is always up to date */
    }

    public boolean isMovable() {
        return false;
    }
}
