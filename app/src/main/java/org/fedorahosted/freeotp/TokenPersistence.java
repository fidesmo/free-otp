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

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;

public interface TokenPersistence {
    public Token addWithToast(Context ctx, String uri);
    public int length();
    public Token get(int position);
    public void add(Token token) throws TokenUriInvalidException;
    public void move(int fromPosition, int toPosition);
    public void delete(int position);
    public void save(Token token);
    public boolean isMovable();
}
