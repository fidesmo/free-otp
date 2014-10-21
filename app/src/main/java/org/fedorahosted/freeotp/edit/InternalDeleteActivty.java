package org.fedorahosted.freeotp.edit;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.Token;
import org.fedorahosted.freeotp.TokenPersistence;
import org.fedorahosted.freeotp.TokenPersistenceFactory;

import java.io.IOException;

public class InternalDeleteActivty extends DeleteActivity {

    private TokenPersistence mTokenPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internal_delete);

        mTokenPersistence = TokenPersistenceFactory.createInternal(this);
        showToken();

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTokenPersistence.delete(getPosition());
                } catch(IOException ioe) {

                }
                finish();
            }
        });
    }

    private void showToken() {
        Token token = mTokenPersistence.get(getPosition());
        ((TextView) findViewById(R.id.issuer)).setText(token.getIssuer());
        ((TextView) findViewById(R.id.label)).setText(token.getLabel());
        Picasso.with(this)
                .load(token.getImage())
                .placeholder(R.drawable.logo)
                .into((ImageView) findViewById(R.id.image));
    }
}
