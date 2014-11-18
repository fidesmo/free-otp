package org.fedorahosted.freeotp.edit;

import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.Token;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DeleteActivity extends Activity {
    public static final String EXTRA_TOKEN = "token";
    public static final String EXTRA_POSITION = "position";
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the position of the token. This MUST exist.
        mPosition = getIntent().getIntExtra(EXTRA_POSITION, -1);
        assert mPosition >= 0;
        Token token = (Token)getIntent().getSerializableExtra(EXTRA_TOKEN);
        setContentView(R.layout.delete);

        ((TextView) findViewById(R.id.issuer)).setText(token.getIssuer());
        ((TextView) findViewById(R.id.label)).setText(token.getLabel());
        Picasso.with(this)
                .load(token.getImage())
                .placeholder(R.drawable.logo)
                .into((ImageView) findViewById(R.id.image));

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultData = new Intent();
                resultData.putExtra(EXTRA_POSITION, mPosition);
                setResult(RESULT_OK, resultData);
                finish();
            }
        });
    }
}
