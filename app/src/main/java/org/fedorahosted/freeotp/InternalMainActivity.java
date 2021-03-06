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

/*
 * Portions Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp;

import static org.fedorahosted.freeotp.NfcHelpers.*;
import org.fedorahosted.freeotp.edit.EditActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.widget.GridView;
import android.view.View;
import android.widget.Toast;

public class InternalMainActivity extends MainActivity {
    private InternalTokenPersistence mTokenPersistence;
    private TokenAdapter mTokenAdapter;
    private DataSetObserver mDataSetObserver;
    private NfcAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mTokenPersistence = new InternalTokenPersistence(this);
        mTokenAdapter = new TokenAdapter(this, mTokenPersistence);
        ((GridView) findViewById(R.id.grid)).setAdapter(mTokenAdapter);

        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mTokenAdapter.getCount() == 0)
                    findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                else
                    findViewById(android.R.id.empty).setVisibility(View.GONE);
            }
        };
        mTokenAdapter.registerDataSetObserver(mDataSetObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTokenAdapter.notifyDataSetChanged();
        enableForegroundDispatch(this, mAdapter, new Intent(this, ExternalMainActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTokenAdapter.notifyDataSetChanged();
        disableForegroundDispatch(this, mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTokenAdapter.unregisterDataSetObserver(mDataSetObserver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            mTokenPersistence.addWithToast(this, uri.toString());
        }
    }

    protected void deleteToken(int position) {
        mTokenPersistence.delete(position);
    }

    protected void addToken(String tokenUri) {
        mTokenPersistence.addWithToast(this, tokenUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            InternalToken token = (InternalToken)data.getSerializableExtra(EditActivity.EXTRA_TOKEN);
            mTokenPersistence.save(token);
        }
    }

}
