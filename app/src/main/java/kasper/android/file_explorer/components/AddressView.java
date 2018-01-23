package kasper.android.file_explorer.components;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.listeners.OnAddressViewRequestListener;
import kasper.android.file_explorer.utils.FileUtils;
import kasper.android.file_explorer.utils.HWareUtils;

public class AddressView extends HorizontalScrollView {

    private String address;

    private int pathPartLRPad;
    private int sepLRPad;
    private int setTBPad;

    private OnAddressViewRequestListener addressViewRequestListener;
    private LinearLayout addressLayout;

    public AddressView(Context context) {
        super(context);
        init(context);
    }

    public AddressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.addressLayout = new LinearLayout(context);
        addressLayout.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.MATCH_PARENT));
        addressLayout.setOrientation(LinearLayout.HORIZONTAL);
        addressLayout.setPadding((int)(16 * HWareUtils.getScreenDensity()), 0, 0, 0);
        this.addView(addressLayout);

        this.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        pathPartLRPad = (int)(8 * HWareUtils.getScreenDensity());
        sepLRPad = (int)(8 * HWareUtils.getScreenDensity());
        setTBPad = (int)((46 - 35) * HWareUtils.getScreenDensity()) / 2 + sepLRPad;
    }

    public void setInteractionInterface(OnAddressViewRequestListener addressViewRequestListener) {
        this.addressViewRequestListener = addressViewRequestListener;
    }

    public void setAddress(String address) {

        this.address = address;

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                AddressView.this.addressLayout.removeAllViews();

                if (AddressView.this.address.equals("/storage/apps") || AddressView.this.address.equals("/storage/photos")
                        || AddressView.this.address.equals("/storage/musics") || AddressView.this.address.equals("/storage/videos")) {

                    AddressView.this.address = AddressView.this.address.substring(1);

                    String[] visibleAddressParts = AddressView.this.address.split("/");

                    TextView button0 = makeBTN(visibleAddressParts[0]);

                    AddressView.this.addressLayout.addView(button0);

                    for (int counter = 1; counter < visibleAddressParts.length; counter++) {

                        AddressView.this.addressLayout.addView(makeSeparator());

                        AddressView.this.addressLayout.addView(makeBTN(visibleAddressParts[counter]));
                    }
                }
                else {

                    if (AddressView.this.address.startsWith(FileUtils.getPrimaryStoragePath())) {
                        AddressView.this.address = "storage/primary" + AddressView.this.address.substring(FileUtils.getPrimaryStoragePath().length());
                    } else if (FileUtils.getSecondaryStoragePath() != null &&
                            FileUtils.getSecondaryStoragePath().length() > 0 &&
                            AddressView.this.address.startsWith(FileUtils.getSecondaryStoragePath())) {
                        AddressView.this.address = "storage/secondary" + AddressView.this.address.substring(FileUtils.getSecondaryStoragePath().length());
                    }

                    if (AddressView.this.address.startsWith("/")) {
                        if (AddressView.this.address.length() > 1) {
                            AddressView.this.address = AddressView.this.address.substring(1);
                        } else {
                            AddressView.this.address = "";
                        }
                    }

                    String[] visibleAddressParts = AddressView.this.address.split("/");

                    String temp = visibleAddressParts[0];

                    TextView button0 = makeBTN(visibleAddressParts[0]);

                    AddressView.this.addressLayout.addView(button0);

                    for (int counter = 1; counter < visibleAddressParts.length; counter++) {

                        AddressView.this.addressLayout.addView(makeSeparator());

                        temp += "/" + visibleAddressParts[counter];

                        TextView button = makeBTN(visibleAddressParts[counter]);

                        if (counter < visibleAddressParts.length - 1) {

                            final String currentPath = temp;

                            button.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (addressViewRequestListener != null) {
                                        String clickPath = currentPath;
                                        if (clickPath.startsWith("storage/primary")) {
                                            clickPath = FileUtils.getPrimaryStoragePath() + clickPath.substring("storage/primary".length());
                                        } else if (clickPath.startsWith("storage/secondary")) {
                                            clickPath = FileUtils.getSecondaryStoragePath() + clickPath.substring("storage/secondary".length());
                                        }
                                        addressViewRequestListener.openPath(clickPath);
                                    }
                                }
                            });
                        }

                        AddressView.this.addressLayout.addView(button);
                    }
                }

                AddressView.this.addressLayout.addView(new FrameLayout(getContext()), new LinearLayout.LayoutParams((int)(100 * HWareUtils.getScreenDensity()), LinearLayout.LayoutParams.MATCH_PARENT));

                AddressView.this.requestLayout();

                AddressView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        AddressView.this.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                });
            }
        });
    }

    private TextView makeBTN(String caption) {

        TextView button = new TextView(getContext());
        button.setText(caption);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setGravity(Gravity.CENTER);
        button.setPadding(pathPartLRPad, 0, pathPartLRPad, 0);

        return button;
    }

    private ImageView makeSeparator() {

        ImageView separator = new ImageView(getContext());
        separator.setLayoutParams(new LinearLayout.LayoutParams((int)(35 * HWareUtils.getScreenDensity()), getHeight()));
        separator.setImageResource(R.drawable.path_seperator);
        separator.setScaleType(ImageView.ScaleType.FIT_XY);
        separator.setPadding(sepLRPad, setTBPad, sepLRPad, setTBPad);

        return separator;
    }
}