/*
 * FreeOTP
 *
 * Authors: Petter Arvidsson <petter.arvidsson@fidesmo.com>
 *
 * Copyright (C) 2014  Petter Arvidsson, Fidesmo AB
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
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.tech.IsoDep;

public class TokenPersistenceFactory {

    public static TokenPersistence createInternal(Context ctx) {
        return new InternalTokenPersistence(ctx);
    }

    public static TokenPersistence createExternal(Context ctx, IsoDep tag) throws IOException {
        return new ExternalTokenPersistence(tag);
    }

    public static TokenPersistence create(Context ctx, IsoDep tag) throws IOException {
        if(tag != null) {
            return createExternal(ctx, tag);
        } else {
            return createInternal(ctx);
        }
    }
}
