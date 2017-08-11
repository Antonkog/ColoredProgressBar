# ColoredProgressBar
Simple view that helps to make gradient on ProgressBar

How to use:
Add dependency in your build.gradle file app folder:

    compile 'com.github.Antonkog:ColoredProgressBar:0.0.1'

Add it to your layout:


    <com.redhotapp.gradientprogresbar.GradientProgressBar
        android:id="@+id/color_progress"
        android:layout_width="match_parent"
        android:layout_height="30dp" />


 GradientProgressBar bar = (GradientProgressBar) findViewById(R.id.color_progress);

        // set colors and weights
        Pair<Integer, Float> colorsAndWeight[] = new Pair[]{
                new Pair<>(Color.RED, 0.02f),
                new Pair<>(Color.RED, 0.08f),
                new Pair<>(Color.YELLOW, 0.1f),
                new Pair<>(Color.GREEN, 0.1f),
                new Pair<>(Color.BLUE, 0.2f),
                new Pair<>(Color.BLUE, 0.2f),
                new Pair<>(Color.GREEN, 0.1f),
                new Pair<>(Color.YELLOW, 0.1f),
                new Pair<>(Color.RED, 0.1f)
        };

        //or set colors without weights
        //int [] colors = {Color.BLUE, Color.RED, Color.GRAY, Color.GREEN};
        // bar.setColors(colors);

        bar.setPointsNumber(100);
        // bar.setTextSize(14);
        bar.setDividersColor(Color.WHITE);
        bar.setCentralCircleColor(getResources().getColor(R.color.colorPrimary));
        bar.setDividerWidthPx(2);
        bar.setColorAndWeight(colorsAndWeight);
        // bar.setDividersVisible(false);
        //bar.setSmallCircleVisible(false);
        //bar.setTextVisible(false);
        bar.setProgress(50);
        //  bar.setStyle(GradientProgressBar.STYLE.PIMP);


