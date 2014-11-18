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

import java.io.IOException;

import org.fedorahosted.freeotp.add.ScanActivity;
import org.fedorahosted.freeotp.add.AddActivity;
import org.fedorahosted.freeotp.add.BaseActivity;
import org.fedorahosted.freeotp.edit.DeleteActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public abstract class MainActivity extends Activity implements OnMenuItemClickListener {
    public static final int DELETE_REQUEST_CODE = 1;
    public static final int SCAN_REQUEST_CODE = 2;
    public static final int ADD_REQUEST_CODE = 3;
    public static final int EDIT_REQUEST_CODE = 4;
    protected abstract void deleteToken(int position);
    protected abstract void addToken(String tokenUri);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Don't permit screenshots since these might contain OTP codes.
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_scan).setVisible(ScanActivity.haveCamera());
        menu.findItem(R.id.action_scan).setOnMenuItemClickListener(this);
        menu.findItem(R.id.action_add).setOnMenuItemClickListener(this);
        menu.findItem(R.id.action_about).setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
        case R.id.action_scan:
            i = new Intent(this, ScanActivity.class);
            startActivityForResult(i, SCAN_REQUEST_CODE);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            return true;

        case R.id.action_add:
            startActivityForResult(new Intent(this, AddActivity.class), ADD_REQUEST_CODE);
            return true;

        case R.id.action_about:
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
            case DELETE_REQUEST_CODE:
                int position = data.getIntExtra(DeleteActivity.EXTRA_POSITION, -1);
                deleteToken(position);
                break;
            case ADD_REQUEST_CODE:
            case SCAN_REQUEST_CODE:
                String tokenUri = data.getStringExtra(BaseActivity.EXTRA_TOKEN_URI);
                addToken(tokenUri);
                break;
            }
        }
    }

}
