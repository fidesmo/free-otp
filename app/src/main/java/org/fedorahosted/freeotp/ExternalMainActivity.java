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

import org.fedorahosted.freeotp.add.ExternalAddActivity;
import static org.fedorahosted.freeotp.NfcHelpers.*;

import android.content.Intent;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.GridView;
import android.view.View;
import android.os.Bundle;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;

public class ExternalMainActivity extends MainActivity {
    private NfcAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, ExternalMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        enableForegroundDispatch(this, mAdapter, intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, mAdapter);
        setUpTokenList(null);
    }

    private void setUpTokenList(IsoDep tag) {
        TokenPersistence persistence = null;
        TextView view = (TextView)findViewById(android.R.id.empty);
        if(tag != null) {
            try {
                persistence = TokenPersistenceFactory.createExternal(this, tag);
            } catch(IOException ioe) {
                Toast.makeText(this, R.string.failed_to_read_external_token, Toast.LENGTH_SHORT).show();
            }
        }

        if(persistence != null) {
            view.setText(R.string.no_keys);
            getActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.color.black));
            /*
             * The following two lines are a fix for the following bug:
             * http://stackoverflow.com/questions/17076958/change-actionbar-color-programmatically-more-then-once
             */
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);
            TokenAdapter adapter = new TokenAdapter(this, persistence);
            ((GridView) findViewById(R.id.grid)).setAdapter(adapter);
            if (adapter.getCount() == 0)
                view.setVisibility(View.VISIBLE);
            else
                view.setVisibility(View.GONE);
        } else {
            view.setText(R.string.tap_external_token);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setUpTokenList(getIsoTag(intent));
    }

    protected Class getAddActivity() {
        return ExternalAddActivity.class;
    }

    protected boolean isInternal() {
        return false;
    }

}
