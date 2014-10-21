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

package org.fedorahosted.freeotp.add;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.apps.authenticator.Base32String;

import org.fedorahosted.freeotp.InternalToken;
import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.Token;
import org.fedorahosted.freeotp.TokenPersistenceFactory;

import java.io.IOException;

import static org.fedorahosted.freeotp.NfcHelpers.disableForegroundDispatch;
import static org.fedorahosted.freeotp.NfcHelpers.enableForegroundDispatch;
import static org.fedorahosted.freeotp.NfcHelpers.getIsoTag;

public class ExternalAddActivity extends AddActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private NfcAdapter mAdapter;
    private IsoDep mTag;
    private boolean inputIsValid = false;
    public final static String EXTRA_TAG = "externalAddActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.add).setVisibility(View.GONE);
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if(getIntent().hasExtra(EXTRA_TAG)){
            prefillFields();
        }
    }

    private void prefillFields() {
        String uriString = getIntent().getStringExtra(EXTRA_TAG);
        try {
            InternalToken internalToken = new InternalToken(uriString);

            EditText mIssuer = (EditText) findViewById(R.id.issuer);
            mIssuer.setText(internalToken.getIssuer());

            EditText mLabel = (EditText) findViewById(R.id.label);
            mLabel.setText(internalToken.getLabel());

            EditText mSecret = (EditText) findViewById(R.id.secret);
            mSecret.setText(Base32String.encode(internalToken.getSecret()));

            EditText mInterval = (EditText) findViewById(R.id.interval);
            mInterval.setText(String.valueOf(internalToken.getPeriod()));

            RadioButton radiohotp = (RadioButton) findViewById(R.id.hotp);
            RadioButton radiototp = (RadioButton) findViewById(R.id.totp);
            if (internalToken.getType() == Token.TokenType.HOTP){
                radiohotp.setChecked(true);
                radiototp.setChecked(false);
            } else {
                radiohotp.setChecked(false);
                radiototp.setChecked(true);
            }

            RadioButton radio6 = (RadioButton) findViewById(R.id.digits6);
            RadioButton radio8 = (RadioButton) findViewById(R.id.digits8);
            if (internalToken.getDigits() == 6){
                radio6.setChecked(true);
                radio8.setChecked(false);
            } else {
                radio6.setChecked(false);
                radio8.setChecked(true);
            }

            EditText mCounter = (EditText) findViewById(R.id.counter);
            mCounter.setText(String.valueOf(internalToken.getCounter()));

        } catch (Token.TokenUriInvalidException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void validationIsValid(boolean isValid) {
        inputIsValid = isValid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch(this, mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, mAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(inputIsValid) {
            mTag = getIsoTag(intent);
            if (mTag != null) addToken();
        } else {
            Toast.makeText(this, getString(R.string.external_add_token_validation_wrong), Toast.LENGTH_LONG).show();
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
