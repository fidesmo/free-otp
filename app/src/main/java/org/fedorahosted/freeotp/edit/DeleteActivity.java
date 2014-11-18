package org.fedorahosted.freeotp.edit;

import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.InternalToken;
import org.fedorahosted.freeotp.InternalTokenPersistence;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DeleteActivity extends BaseActivity {
    private InternalTokenPersistence mTokenPersistence;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete);
        mTokenPersistence = new InternalTokenPersistence(this);
        InternalToken token = mTokenPersistence.get(getPosition());
        ((TextView) findViewById(R.id.issuer)).setText(token.getIssuer());
        ((TextView) findViewById(R.id.label)).setText(token.getLabel());
        Picasso.with(this)
                .load(token.getImage())
                .placeholder(R.drawable.logo)
                .into((ImageView) findViewById(R.id.image));

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTokenPersistence.delete(getPosition());
                finish();
            }
        });
    }
}
