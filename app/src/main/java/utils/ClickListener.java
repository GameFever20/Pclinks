package utils;

import android.view.View;

/**
 * Created by bunny on 14/06/17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
