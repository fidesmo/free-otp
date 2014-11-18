/*
 * FreeOTP
 *
 * Authors: Nathaniel McCallum <npmccallum@redhat.com>
 *
 * Copyright (C) 2013  Nathaniel McCallum, Red Hat
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

import android.net.Uri;

public interface Token {
    public static class TokenUriInvalidException extends Exception {
        private static final long serialVersionUID = -1108624734612362345L;
    }

    public static enum TokenType {
        HOTP, TOTP
    }

    public String getID();

    public String getIssuer();

    public void setIssuer(String issuer);

    public String getLabel();

    public void setLabel(String label);

    public int getDigits();

    // NOTE: This may change internal data. You MUST save the token immediately.
    public TokenCode generateCodes();

    public TokenType getType();

    public Uri getImage();

    public void setImage(Uri image);

    public boolean isInternal();
}
