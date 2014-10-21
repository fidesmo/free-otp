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

package org.fedorahosted.freeotp.add;

import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.TokenPersistenceFactory;

import java.io.IOException;

public class InternalAddActivity extends AddActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private IsoDep mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.add).setEnabled(false);
    }

    @Override
    protected void validationIsValid(boolean isValid) {
        if (isValid){
            findViewById(R.id.add).setEnabled(true);
        } else {
            findViewById(R.id.add).setEnabled(false);
        }
    }

    @Override
    protected void saveToken(String uri) {
        try {
            // Add the token
            if (TokenPersistenceFactory.create(this, mTag).addWithToast(this, uri) != null) {
                finish();
            }
        } catch(IOException e) {
        }
    }
}
