package f06.medipal.consumption;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import f06.medipal.R;

public class ConsumptionMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String heading = getResources().getString(R.string.consumption_heading);
        this.setTitle(heading) ;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_consumption_main, new ConsumptionFragment())
                .commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
