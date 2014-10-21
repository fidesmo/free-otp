/*
 * FreeOTP
 *
 * Authors: Theo Franzén <theo.franzen@fidesmo.com>
 *
 * Copyright (C) 2014 Theo Franzén, Fidesmo AB
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

package org.fedorahosted.freeotp.edit;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.view.View;

import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.TokenPersistence;
import org.fedorahosted.freeotp.TokenPersistenceFactory;

import java.io.IOException;

import static org.fedorahosted.freeotp.NfcHelpers.*;

public class ExternalDeleteActivity extends DeleteActivity {

    private NfcAdapter adapter;
    private TokenPersistence mTokenPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = NfcAdapter.getDefaultAdapter(this);
        setContentView(R.layout.external_delete);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch(this, adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        IsoDep isoTag = getIsoTag(intent);
        if (isoTag != null) {
            try {
                mTokenPersistence = TokenPersistenceFactory.createExternal(this, isoTag);
                mTokenPersistence.delete(getPosition());
            } catch(IOException ioe) {
            }
            finish();
        }
    }
}
