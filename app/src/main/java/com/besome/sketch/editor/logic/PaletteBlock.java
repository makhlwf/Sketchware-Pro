package com.besome.sketch.editor.logic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import a.a.a.Rs;
import a.a.a.Ts;
import a.a.a.wB;
import pro.sketchware.databinding.PaletteBlockBinding;

public class PaletteBlock extends LinearLayout {

    public float f = 0.0F;
    private PaletteBlockBinding binding;
    private Context context;

    public PaletteBlock(Context context) {
        super(context);
        initialize(context);
    }

    public PaletteBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;
        binding = PaletteBlockBinding.inflate(LayoutInflater.from(context), this, true);
        f = wB.a(context, 1.0F);
    }

    public Ts a(String var1, String var2, String var3) {
        View view = new View(context);
        view.setLayoutParams(getLayoutParams(8.0F));
        binding.blockBuilder.addView(view);
        Rs blockView = new Rs(context, -1, var1, var2, var3);
        blockView.setContentDescription(generateContentDescription(var3));
        blockView.setBlockType(1);
        binding.blockBuilder.addView(blockView);
        return blockView;
    }

    public Ts a(String var1, String var2, String var3, String var4) {
        View view = new View(context);
        view.setLayoutParams(getLayoutParams(8.0F));
        binding.blockBuilder.addView(view);
        Rs blockView = new Rs(context, -1, var1, var2, var3, var4);
        blockView.setContentDescription(generateContentDescription(var4));
        blockView.setBlockType(1);
        binding.blockBuilder.addView(blockView);
        return blockView;
    }

    public TextView a(String title) {
        var textView = new TextView(context);
        textView.setText(title);
        textView.setTextSize(10.0F);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding((int) (f * 8.0F), 0, (int) (f * 8.0F), 0);

        var cardView = new MaterialCardView(context);
        var params = getLayoutParams(30.0F);
        params.setMargins(0, 0, 3, 3);
        cardView.setLayoutParams(params);

        cardView.setCardElevation(0f); // I don't remember if the card has elevation by default 
        cardView.setRadius(12.0f);
        cardView.setStrokeWidth(0);
        cardView.addView(textView);
        binding.actionsContainer.addView(cardView);
        return textView;
    }

    public void a() {
        binding.blockBuilder.removeAllViews();
        binding.actionsContainer.removeAllViews();
    }

    public void a(String title, int color) {
        TextView textView = new TextView(context);
        LayoutParams layoutParams = getLayoutParams(16.0F);
        layoutParams.topMargin = (int) (f * 16.0F);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(color);
        textView.setText(title);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(9.0F);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.TOP | Gravity.LEFT);
        textView.setPadding((int) (f * 12.0F), 0, (int) (f * 12.0F), 0);
        binding.blockBuilder.addView(textView);
    }

    public void addDeprecatedBlock(String message, String type, String opCode) {
        if (message != null && !message.isEmpty()) {
            a(message, 0xff555555);
        }
        Ts blockView = a("", type, opCode);
        blockView.e = 0xFFBDBDBD;
        blockView.setTag(opCode);
    }

    private String generateContentDescription(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        // Specific workaround for "toast"
        if ("toast".equalsIgnoreCase(name)) {
            return "Toast";
        }

        // Pre-process the name: replace underscores with spaces.
        // This helps if opCodes are like "read_file_at_path".
        String processedName = name.replace('_', ' ');

        StringBuilder result = new StringBuilder();

        // Ensure the result doesn't start with a space if the first char of processedName is a space
        int startIndex = 0;
        while(startIndex < processedName.length() && processedName.charAt(startIndex) == ' ') {
            startIndex++;
        }

        if (startIndex < processedName.length()) {
            // Append the first non-space character
            result.append(processedName.charAt(startIndex));
        } else {
            // The string was all spaces or empty after underscore replacement
            return "";
        }

        for (int i = startIndex + 1; i < processedName.length(); i++) {
            char currentChar = processedName.charAt(i);
            char prevChar = processedName.charAt(i - 1); // Safe, because we start from startIndex + 1

            if (currentChar == ' ') {
                // If current is a space, only add it if the last char in result is not already a space.
                if (result.length() > 0 && result.charAt(result.length() - 1) != ' ') {
                    result.append(' ');
                }
            } else if (Character.isUpperCase(currentChar)) {
                if (Character.isLowerCase(prevChar)) {
                    // Case: lowerUpper (e.g., "setActivity" -> "set Activity")
                    // Add space only if prevChar was not a space itself
                    if (prevChar != ' ') result.append(' ');
                } else if (Character.isUpperCase(prevChar) && prevChar != ' ') {
                    // Case: UpperUpper (e.g., "XMLHTTPRequest")
                    // Add space if current char is followed by a lowercase letter (e.g., before 'H' in XMLHttp)
                    if (i + 1 < processedName.length() && Character.isLowerCase(processedName.charAt(i + 1))) {
                        result.append(' ');
                    }
                }
                // If prevChar was a space, no need to add another space here.
                result.append(currentChar);
            } else if (Character.isLowerCase(currentChar)) {
                // If prevChar was an uppercase letter AND part of an acronym (e.g. G in JPEG, current is d in JPEGdemo)
                // and no space was added before, it means prevChar was the end of an acronym.
                // This case is complex. The UpperCase logic should handle most splits.
                // Example: "JPEGDemo" -> "JPEG" then space then "Demo"
                // The previous iteration handles space before D in JPEGDemo if JPEG is followed by Demo.
                result.append(currentChar);
            } else if (Character.isDigit(currentChar)) {
                // Add space before a digit if the previous char was a letter and not a space
                if (Character.isLetter(prevChar) && prevChar != ' ') {
                    result.append(' ');
                }
                result.append(currentChar);
            } else {
                // Other symbols. Add a space if previous char was not a space and current is not a space.
                // This helps separate symbols from words, e.g. "word.symbol" -> "word . symbol"
                if (prevChar != ' ' && result.charAt(result.length() -1) != ' ') {
                     result.append(' ');
                }
                result.append(currentChar);
            }
        }
        // Final cleanup: trim and replace multiple spaces with a single space.
        return result.toString().trim().replaceAll(" +", " ");
    }

    private LinearLayout.LayoutParams getLayoutParams(float heightMultiplier) {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (f * heightMultiplier)
        );
    }

    public void setDragEnabled(boolean dragEnabled) {
        if (dragEnabled) {
            binding.scroll.b();
            binding.scrollHorizontal.b();
        } else {
            binding.scroll.a();
            binding.scrollHorizontal.a();
        }
    }

    public void setMinWidth(int minWidth) {
        binding.scroll.setMinimumWidth(minWidth - (int) (f * 5.0F));
        binding.scrollHorizontal.setMinimumWidth(minWidth - (int) (f * 5.0F));
        getLayoutParams().width = minWidth;
    }

    public void setUseScroll(boolean useScroll) {
        binding.scroll.setUseScroll(useScroll);
        binding.scrollHorizontal.setUseScroll(useScroll);
    }
}
