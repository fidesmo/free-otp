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

import java.io.IOException;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.fedorahosted.freeotp.edit.DeleteActivity;
import org.fedorahosted.freeotp.edit.EditActivity;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class TokenAdapter extends BaseReorderableAdapter {
    private final TokenPersistence mTokenPersistence;
    private final LayoutInflater mLayoutInflater;
    private final ClipboardManager mClipMan;
    private final Map<String, TokenCode> mTokenCodes;
    private final Activity mCtx;

    public TokenAdapter(Activity ctx, TokenPersistence tokenPersistence) {
        mCtx = ctx;
        mTokenPersistence = tokenPersistence;
        mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mClipMan = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        mTokenCodes = new HashMap<String, TokenCode>();
        registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mTokenCodes.clear();
            }

            @Override
            public void onInvalidated() {
                mTokenCodes.clear();
            }
        });
    }

    @Override
    public int getCount() {
        return mTokenPersistence.length();
    }

    @Override
    public Token getItem(int position) {
        return mTokenPersistence.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void move(int fromPosition, int toPosition) {
        mTokenPersistence.move(fromPosition, toPosition);
        notifyDataSetChanged();
    }

    @Override
    protected void bindView(View view, final int position) {
        TokenLayout tl = (TokenLayout) view;
        final Token token = getItem(position);

        int menu = token.isInternal() ? R.menu.editable_token : R.menu.token;

        tl.bind(token, menu, new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i;

                switch (item.getItemId()) {
                    case R.id.action_edit:
                        i = new Intent(mCtx, EditActivity.class);
                        i.putExtra(EditActivity.EXTRA_TOKEN, token);
                        mCtx.startActivityForResult(i, MainActivity.EDIT_REQUEST_CODE);
                        break;

                    case R.id.action_delete:
                        i = new Intent(mCtx, DeleteActivity.class);
                        i.putExtra(DeleteActivity.EXTRA_POSITION, position);
                        i.putExtra(DeleteActivity.EXTRA_TOKEN, token);
                        mCtx.startActivityForResult(i, MainActivity.DELETE_REQUEST_CODE);
                        break;
                }

                return true;
            }
        });

        tl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TokenPersistence tp = TokenAdapter.this.mTokenPersistence;

                // Increment the token.
                Token token = tp.get(position);
                try {
                    TokenCode codes = tp.generateCodes(position);

                    // Copy code to clipboard.
                    mClipMan.setPrimaryClip(ClipData.newPlainText(null, codes.getCurrentCode()));
                    Toast.makeText(v.getContext().getApplicationContext(),
                                   R.string.code_copied,
                                   Toast.LENGTH_SHORT).show();

                    mTokenCodes.put(token.getID(), codes);
                    ((TokenLayout) v).start(token.getType(), codes, true);
                } catch(IOException e) {
                    Toast.makeText(v.getContext().getApplicationContext(),
                            R.string.external_token_needed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        TokenCode tc = mTokenCodes.get(token.getID());
        if (tc != null && tc.getCurrentCode() != null)
            tl.start(token.getType(), tc, false);
    }

    @Override
    protected View createView(ViewGroup parent, int type) {
        return mLayoutInflater.inflate(R.layout.token, parent, false);
    }

    @Override
    protected boolean isMovable() {
        return mTokenPersistence.isMovable();
    }
}
