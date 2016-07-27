package ca.matthewsun.scorecounter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> teams;
    private ArrayList<Integer> scores;
    private LinearLayout parentLayout;
    private String[] scoreClickDialog = {"Add", "Set", "Subtract", "Reset"};
    private String[] sortDialog = {"A to Z", "Z to A", "Highest Score", "Lowest Score"};
    private float marginConvertor;
    private LinearLayout.LayoutParams relativeLayoutParams;
    private RelativeLayout.LayoutParams scoreCountParams, teamNameParams, buttonOneParams, buttonTwoParams, buttonThreeParams, editTextParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        marginConvertor = getResources().getDisplayMetrics().density;
        teams = new ArrayList<>();
        scores = new ArrayList<>();
        parentLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        setTitle("Score Counter");

        // Layout Parameters
        relativeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.CustomAlertDialog));
                builder.setTitle("New Team");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                input.setTextColor(Color.WHITE);
                input.setHint("Enter team name");
                input.setHintTextColor(Color.WHITE);
                input.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(input);

                // Set up the OK button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String s = input.getText().toString();

                        // If the team name is not taken
                        if (!checkDuplicate(s)) {
                            teams.add(s);
                            scores.add(0);

                            // Add a layout encapsulating the views
                            RelativeLayout teamLayout = new RelativeLayout(MainActivity.this);
                            teamLayout.setLayoutParams(relativeLayoutParams);
//                            teamLayout.setBackgroundColor(Color.BLUE);
                            parentLayout.addView(teamLayout);

                            // There is more than one team
                            if (teams.size() > 1) {
                                View divider = new View (MainActivity.this);
                                divider.setBackgroundColor(Color.LTGRAY);

                                teamLayout.addView(divider);
                                divider.getLayoutParams().height = 5;
                                divider.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

                            }

                            // Team name TextView
                            final TextView teamName = new TextView(MainActivity.this);
                            teamName.setText(s);

                            // Set text size to 42 dp
                            teamName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 42);
//                            teamName.setTextColor(Color.BLACK);
                            teamName.setTypeface(Typeface.SANS_SERIF);
                            teamName.setId(R.id.teamName);
//                            teamName.setBackgroundColor(Color.RED);

                            teamLayout.addView(teamName);

                            teamNameParams = (RelativeLayout.LayoutParams)teamName.getLayoutParams();
                            teamNameParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                            teamNameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            teamNameParams.setMargins(0, (int)(16 * marginConvertor), 0, 0);
                            teamName.setLayoutParams(teamNameParams);

                            // When user taps on the team name, allow them to change their name
                            teamName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick (View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.CustomAlertDialog));
                                    builder.setTitle("Change Team Name");
                                    final EditText editText = new EditText(MainActivity.this);
                                    editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                                    editText.setTextColor(Color.WHITE);
                                    editText.setHint("Enter new name");
                                    editText.setHintTextColor(Color.WHITE);
                                    editText.setGravity(Gravity.CENTER_HORIZONTAL);
                                    builder.setView(editText);

                                    // Set up the OK and CANCEL buttons
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String n = editText.getText().toString();
                                            if (!checkDuplicate((n))) {
                                                teams.set(teams.indexOf(teamName.getText()), n);
                                                teamName.setText(n);

                                            }
                                            // Team name is taken
                                            else {
                                                Toast.makeText(MainActivity.this, "Name is taken", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    // Set up the CANCEL button
                                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });

                                    AlertDialog nameChanger = builder.create();
                                    nameChanger.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                    nameChanger.show();

                                }
                            });

                            // Score Count
                            final TextView scoreCount = new TextView(MainActivity.this);
                            scoreCount.setText("0");
                            scoreCount.setId(R.id.scoreCount);
                            scoreCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 108);
                            scoreCount.setTypeface(Typeface.SANS_SERIF);
                            teamLayout.addView(scoreCount);

                            scoreCountParams = (RelativeLayout.LayoutParams)scoreCount.getLayoutParams();
                            scoreCountParams.addRule(RelativeLayout.BELOW, teamName.getId());
                            scoreCountParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                            scoreCountParams.setMargins(0, (int)(8 * marginConvertor), (int)(64 * marginConvertor), 0);
                            scoreCount.setLayoutParams(scoreCountParams);

                            scoreCount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                    // Create the number input for the ADD, EDIT, SUBTRACT options
                                    final AlertDialog.Builder numberInputBuilder = new AlertDialog.Builder(MainActivity.this);
                                    final EditText numberInput = new EditText(MainActivity.this);
                                    numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    numberInput.setGravity(Gravity.CENTER_HORIZONTAL);
                                    numberInputBuilder.setView(numberInput);
                                    builder.setItems(scoreClickDialog, new DialogInterface.OnClickListener() {
                                        public void onClick (DialogInterface dialog, int which) {
                                            // User clicks on ADD
                                            if (which == 0) {
                                                numberInputBuilder.setTitle("Add");
                                                numberInput.setHint(R.string.add_hint);
                                                numberInputBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        scoreCount.setText(String.valueOf(addCustomAmount(teamName.getText().toString(), Integer.parseInt(numberInput.getText().toString()))));
                                                    }
                                                });

                                                numberInputBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.cancel();
                                                    }
                                                });

                                                AlertDialog numberDialog = numberInputBuilder.create();
                                                numberDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                numberDialog.show();
                                            }
                                            // User clicks on SET
                                            else if (which == 1) {
                                                numberInputBuilder.setTitle("Set");
                                                numberInput.setHint(R.string.edit_hint);
                                                numberInputBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        scoreCount.setText(String.valueOf(editScoreAmount(teamName.getText().toString(), Integer.parseInt(numberInput.getText().toString()))));
                                                    }
                                                });

                                                numberInputBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.cancel();
                                                    }
                                                });

                                                AlertDialog numberDialog = numberInputBuilder.create();
                                                numberDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                numberDialog.show();

                                            }
                                            // User clicks on SUBTRACT
                                            else if (which == 2) {
                                                numberInputBuilder.setTitle("Subtract");
                                                numberInput.setHint(R.string.subtract_hint);
                                                numberInputBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        scoreCount.setText(String.valueOf(subtractScoreAmount(teamName.getText().toString(), Integer.parseInt(numberInput.getText().toString()))));
                                                    }
                                                });

                                                numberInputBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.cancel();
                                                    }
                                                });

                                                AlertDialog numberDialog = numberInputBuilder.create();
                                                numberDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                                numberDialog.show();

                                            }
                                            // User clicks on RESET
                                            else if (which == 3) {
                                                resetScore(teamName.getText().toString());
                                                scoreCount.setText("0");
                                            }

                                        }
                                    });
                                    AlertDialog a = builder.create();
                                    a.show();


                                }
                            });


                            // +1 Button
                            Button one = new Button(MainActivity.this);
                            one.setText("+1 Point");
                            one.setId(R.id.buttonOne);
                            one.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    scoreCount.setText(String.valueOf(addOne(teamName.getText().toString())));
                                }
                            });

                            teamLayout.addView(one);

                            buttonOneParams = (RelativeLayout.LayoutParams)one.getLayoutParams();
                            buttonOneParams.addRule(RelativeLayout.BELOW, teamName.getId());
                            buttonOneParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            buttonOneParams.setMargins((int)(32 * marginConvertor), (int)(16 * marginConvertor), 0, 0);
                            one.setLayoutParams(buttonOneParams);

                            // +2 Button
                            Button two = new Button(MainActivity.this);
                            two.setText("+2 Points");
                            two.setId(R.id.buttonTwo);
                            two.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    scoreCount.setText(String.valueOf(addTwo(teamName.getText().toString())));
                                }
                            });
                            teamLayout.addView(two);

                            buttonTwoParams = (RelativeLayout.LayoutParams)two.getLayoutParams();
                            buttonTwoParams.addRule(RelativeLayout.BELOW, one.getId());
                            buttonTwoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            buttonTwoParams.setMargins((int)(32 * marginConvertor), 0, 0, 0);
                            two.setLayoutParams(buttonTwoParams);

                            // +3 Button
                            Button three = new Button(MainActivity.this);
                            three.setText("+3 Points");
                            three.setId(R.id.buttonThree);
                            three.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    scoreCount.setText(String.valueOf(addThree(teamName.getText().toString())));
                                }
                            });
                            teamLayout.addView(three);

                            buttonThreeParams = (RelativeLayout.LayoutParams)three.getLayoutParams();
                            buttonThreeParams.addRule(RelativeLayout.BELOW, two.getId());
                            buttonThreeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            buttonThreeParams.setMargins((int)(32 * marginConvertor), 0, 0, (int)(16 * marginConvertor));
                            three.setLayoutParams(buttonThreeParams);

                        }
                        // Team name is taken
                        else {
                            Toast.makeText(MainActivity.this, "Name is taken", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                // Set up the CANCEL button
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alertDialog.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Remove teams
        if (id == R.id.remove_teams) {
            removeTeam();
            return true;
        }
        // Sort teams
        else if (id == R.id.sort_teams) {
            sortTeams();
            return true;
        }
        // Reset all scores
        else if (id == R.id.reset_all) {
            resetAllScores();
            return true;
        }
        else if (id == R.id.clear_all) {
            clearTeams();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the removal of teams
     */
    private void removeTeam () {
        final String[] teamArray = new String[teams.size()];
        for (int n = 0 ; n < teams.size() ; n ++) {
            teamArray[n] = teams.get(n);
        }

        final ArrayList<String> selectedTeams = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Remove Teams").setMultiChoiceItems(teamArray, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                // If user checks an item, add it to the list of selected teams
                if (isChecked) {
                    selectedTeams.add(teamArray[which]);
                }
                // If the item is already on the list, remove it
                else if (selectedTeams.contains(teamArray[which])) {
                    selectedTeams.remove(teamArray[which]);
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int n = 0 ; n < selectedTeams.size() ; n ++) {
                    removeTeamsFromArray (selectedTeams);
                }
                refresh();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog a = builder.create();
        a.show();
    }

    /**
     * Removes the teams and scores
     * @param t the teams to remove
     */
    private void removeTeamsFromArray (ArrayList<String> t) {
        while (t.size() > 0) {
            int i = teams.indexOf(t.get(0));
            teams.remove(i);
            scores.remove(i);
            t.remove(0);
        }
    }

    /**
     * Display the sorting options
     */
    private void sortTeams() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(sortDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // A to Z team sort
                 if (which == 0) {
                    sortTeamsAZ();
                 }
                // Z to A team sort
                else if (which == 1) {
                     sortTeamsZA();
                 }
                // Highest to lowest score
                else if (which == 2) {
                    sortScoresHighest();
                 }
                // Lowest to highest score
                else if (which == 3) {
                    sortScoresLowest();
                 }
                refresh();
            }
        });
        AlertDialog a = builder.create();
        a.show();
    }

    /**
     * Sorts the teams and scores list A to Z (teams)
     */
   private void sortTeamsAZ () {
        for (int n = 1 ; n < teams.size() ; n ++) {
            int currentIndex = n;

            while(currentIndex > 0 && (teams.get(currentIndex).compareTo(teams.get(currentIndex - 1)) < 0)) {
                String placeholder = teams.get(currentIndex);
                teams.set(currentIndex, teams.get(currentIndex - 1));
                teams.set(currentIndex - 1, placeholder);

                int score = scores.get(currentIndex);
                scores.set(currentIndex, scores.get(currentIndex - 1));
                scores.set(currentIndex - 1, score);

                currentIndex --;
            }
        }
   }

    /**
     * Sorts the teams and scores lists Z to A (teams)
     */
    private void sortTeamsZA () {
        for (int n = 1 ; n < teams.size() ; n ++) {
            int currentIndex = n;

            while(currentIndex > 0 && (teams.get(currentIndex).compareTo(teams.get(currentIndex - 1)) > 0)) {
                String placeholder = teams.get(currentIndex);
                teams.set(currentIndex, teams.get(currentIndex - 1));
                teams.set(currentIndex - 1, placeholder);

                int score = scores.get(currentIndex);
                scores.set(currentIndex, scores.get(currentIndex - 1));
                scores.set(currentIndex - 1, score);

                currentIndex --;
            }
        }
    }

    /**
     * Sorts the scores from highest to lowest
     */
    private void sortScoresHighest () {
        for (int n = 1 ; n < scores.size() ; n ++) {
            int currentIndex = n;

            while(currentIndex > 0 && scores.get(currentIndex) > scores.get(currentIndex - 1)) {
                String placeholder = teams.get(currentIndex);
                teams.set(currentIndex, teams.get(currentIndex - 1));
                teams.set(currentIndex - 1, placeholder);

                int score = scores.get(currentIndex);
                scores.set(currentIndex, scores.get(currentIndex - 1));
                scores.set(currentIndex - 1, score);

                currentIndex --;
            }
        }
    }

    /**
     * Sorts the scores from lowest to highest
     */
    private void sortScoresLowest () {
        for (int n = 1 ; n < scores.size() ; n ++) {
            int currentIndex = n;

            while(currentIndex > 0 && scores.get(currentIndex) < scores.get(currentIndex - 1)) {
                String placeholder = teams.get(currentIndex);
                teams.set(currentIndex, teams.get(currentIndex - 1));
                teams.set(currentIndex - 1, placeholder);

                int score = scores.get(currentIndex);
                scores.set(currentIndex, scores.get(currentIndex - 1));
                scores.set(currentIndex - 1, score);

                currentIndex --;
            }
        }
    }

    /**
     * Clears all teams (views) on the layout
     */
    private void clearTeams() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to clear all?");
        builder.setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                parentLayout.removeAllViews();
//                parentLayout.removeAllViewsInLayout();
                teams.clear();
                scores.clear();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog a = builder.create();
        a.show();
    }

    /**
     * Resets all scores
     */
    private void resetAllScores () {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to reset scores?");
        builder.setPositiveButton("Reset All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int n = 0 ; n < scores.size() ; n ++) {
                    scores.set(n, 0);
                }
                refresh();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog a = builder.create();
        a.show();
    }


    /**
     * Refreshes the layout based on the teams and scores list
     */
    private void refresh() {
        parentLayout.removeAllViews();

        // Rebuild the layout based on the team and score list
        for (int n = 0 ; n < teams.size() ; n ++) {
            // Add a layout encapsulating the views
            RelativeLayout teamLayout = new RelativeLayout(MainActivity.this);
            teamLayout.setLayoutParams(relativeLayoutParams);
            parentLayout.addView(teamLayout);

            // Divide the teams
            if (n > 0) {
                View divider = new View (MainActivity.this);
                divider.setBackgroundColor(Color.LTGRAY);

                teamLayout.addView(divider);
                divider.getLayoutParams().height = 5;
                divider.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            }

            // Team name TextView
            final TextView teamName = new TextView(MainActivity.this);
            teamName.setText(teams.get(n));

            // Set text size to 42 dp
            teamName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 42);
//                            teamName.setTextColor(Color.BLACK);
            teamName.setTypeface(Typeface.SANS_SERIF);
            teamName.setId(R.id.teamName);
//                            teamName.setBackgroundColor(Color.RED);

            teamLayout.addView(teamName);

            teamNameParams = (RelativeLayout.LayoutParams)teamName.getLayoutParams();
            teamNameParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            teamNameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            teamNameParams.setMargins(0, (int)(16 * marginConvertor), 0, 0);
            teamName.setLayoutParams(teamNameParams);

            // When user taps on the team name, allow them to change their name
            teamName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.CustomAlertDialog));
                    builder.setTitle("Change Team Name");
                    final EditText editText = new EditText(MainActivity.this);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    editText.setTextColor(Color.WHITE);
                    editText.setGravity(Gravity.CENTER_HORIZONTAL);
                    editText.setHint("Enter new name");
                    editText.setHintTextColor(Color.WHITE);
                    builder.setView(editText);

                    // Set up the OK and CANCEL buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String n = editText.getText().toString();
                            if (!checkDuplicate((n))) {
                                teams.set(teams.indexOf(teamName.getText()), n);
                                teamName.setText(n);

                            }
                            // Team name is taken
                            else {
                                Toast.makeText(MainActivity.this, "Name is taken", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Set up the CANCEL button
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog nameChanger = builder.create();
                    nameChanger.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    nameChanger.show();

                }
            });

            // Score Count
            final TextView scoreCount = new TextView(MainActivity.this);
            scoreCount.setText(scores.get(n).toString());
            scoreCount.setId(R.id.scoreCount);
            scoreCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 108);
            scoreCount.setTypeface(Typeface.SANS_SERIF);
            teamLayout.addView(scoreCount);

            scoreCountParams = (RelativeLayout.LayoutParams)scoreCount.getLayoutParams();
            scoreCountParams.addRule(RelativeLayout.BELOW, teamName.getId());
            scoreCountParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            scoreCountParams.setMargins(0, (int)(8 * marginConvertor), (int)(64 * marginConvertor), 0);
            scoreCount.setLayoutParams(scoreCountParams);

            scoreCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    // Create the number input for the ADD, EDIT, SUBTRACT options
                    final AlertDialog.Builder numberInputBuilder = new AlertDialog.Builder(MainActivity.this);
                    final EditText numberInput = new EditText(MainActivity.this);
                    numberInput.setGravity(Gravity.CENTER_HORIZONTAL);
                    numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
//                                    numberInput.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    numberInputBuilder.setView(numberInput);
                    builder.setItems(scoreClickDialog, new DialogInterface.OnClickListener() {
                        public void onClick (DialogInterface dialog, int which) {
                            // User clicks on ADD
                            if (which == 0) {
                                numberInputBuilder.setTitle("Add");
                                numberInput.setHint(R.string.add_hint);
                                numberInputBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        scoreCount.setText(String.valueOf(addCustomAmount(teamName.getText().toString(), Integer.parseInt(numberInput.getText().toString()))));
                                    }
                                });

                                numberInputBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                AlertDialog numberDialog = numberInputBuilder.create();
                                numberDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                numberDialog.show();
                            }
                            // User clicks on SET
                            else if (which == 1) {
                                numberInputBuilder.setTitle("Set");
                                numberInput.setHint(R.string.edit_hint);
                                numberInputBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        scoreCount.setText(String.valueOf(editScoreAmount(teamName.getText().toString(), Integer.parseInt(numberInput.getText().toString()))));
                                    }
                                });

                                numberInputBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                AlertDialog numberDialog = numberInputBuilder.create();
                                numberDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                numberDialog.show();

                            }
                            // User clicks on SUBTRACT
                            else if (which == 2) {
                                numberInputBuilder.setTitle("Subtract");
                                numberInput.setHint(R.string.subtract_hint);
                                numberInputBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        scoreCount.setText(String.valueOf(subtractScoreAmount(teamName.getText().toString(), Integer.parseInt(numberInput.getText().toString()))));
                                    }
                                });

                                numberInputBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                AlertDialog numberDialog = numberInputBuilder.create();
                                numberDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                numberDialog.show();

                            }
                            // User clicks on RESET
                            else if (which == 3) {
                                resetScore(teamName.getText().toString());
                                scoreCount.setText("0");
                            }

                        }
                    });
                    AlertDialog a = builder.create();
                    a.show();


                }
            });

            // +1 Button
            Button one = new Button(MainActivity.this);
            one.setText("+1 Point");
            one.setId(R.id.buttonOne);
            one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scoreCount.setText(String.valueOf(addOne(teamName.getText().toString())));
                }
            });

            teamLayout.addView(one);

            buttonOneParams = (RelativeLayout.LayoutParams)one.getLayoutParams();
            buttonOneParams.addRule(RelativeLayout.BELOW, teamName.getId());
            buttonOneParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonOneParams.setMargins((int)(32 * marginConvertor), (int)(16 * marginConvertor), 0, 0);
            one.setLayoutParams(buttonOneParams);

            // +2 Button
            Button two = new Button(MainActivity.this);
            two.setText("+2 Points");
            two.setId(R.id.buttonTwo);
            two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scoreCount.setText(String.valueOf(addTwo(teamName.getText().toString())));
                }
            });
            teamLayout.addView(two);

            buttonTwoParams = (RelativeLayout.LayoutParams)two.getLayoutParams();
            buttonTwoParams.addRule(RelativeLayout.BELOW, one.getId());
            buttonTwoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonTwoParams.setMargins((int)(32 * marginConvertor), 0, 0, 0);
            two.setLayoutParams(buttonTwoParams);

            // +3 Button
            Button three = new Button(MainActivity.this);
            three.setText("+3 Points");
            three.setId(R.id.buttonThree);
            three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scoreCount.setText(String.valueOf(addThree(teamName.getText().toString())));
                }
            });
            teamLayout.addView(three);

            buttonThreeParams = (RelativeLayout.LayoutParams)three.getLayoutParams();
            buttonThreeParams.addRule(RelativeLayout.BELOW, two.getId());
            buttonThreeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            buttonThreeParams.setMargins((int)(32 * marginConvertor), 0, 0, (int)(16 * marginConvertor));
            three.setLayoutParams(buttonThreeParams);

        }



    }



    /**
     * Checks whether or not the given team name is taken
     * @param s the String to check
     * @return whether or not the String already exists in the list
     */
    private boolean checkDuplicate (String s) {
        return teams.contains(s);
//        for (int n = 0 ; n < teams.size() ; n ++) {
//            if (teams.get(n).equalsIgnoreCase(s))
//                return true;
//        }
//        return false;
    }

    /**
     * Sets a teams score to a custom amount
     * @param teamName the team score to change
     * @param amount the amount to change to
     * @return the new score or -1 if team name DNE
     */
    private int editScoreAmount (String teamName, int amount) {
        for (int n = 0 ; n < teams.size() ; n ++) {
            if (teamName.equalsIgnoreCase(teams.get(n))) {
                scores.set(n, amount);
                return scores.get(n);
            }
        }
        return -1;
    }

    /**
     * Subtracts a team score be amount
     * @param teamName the team score to subtract
     * @param amount the amount to subtract
     * @return the new score or -1 if team name DNE
     */
    private int subtractScoreAmount (String teamName, int amount) {
        for (int n = 0 ; n < teams.size() ; n ++) {
            if (teamName.equalsIgnoreCase(teams.get(n))) {
                scores.set(n, scores.get(n) - amount);
                return scores.get(n);
            }
        }
        return -1;
    }


    /**
     * Adds a custom amount to a given team
     * @param teamName the team to add to
     * @param amount the amount to add
     * @return the new score after adding or -1 if team name DNE
     */
    private int addCustomAmount(String teamName, int amount) {
        for (int n = 0 ; n < teams.size() ; n ++) {
            if (teamName.equalsIgnoreCase(teams.get(n))) {
                scores.set(n, scores.get(n) + amount);
                return scores.get(n);
            }
        }
        return -1;
    }


    /**
     * Adds a point to a given team and returns their score
     * @param teamName the team to add to
     * @return the new score after adding one or -1 if team name doesn't exist
     */
    private int addOne (String teamName) {
        for (int n = 0 ; n < teams.size() ; n ++) {
            if (teamName.equalsIgnoreCase(teams.get(n))) {
                scores.set(n, scores.get(n) + 1);
                return scores.get(n);
            }
        }
        return -1;
    }

    /**
     * Adds two points to a given team and returns their score
     * @param teamName the team to add to
     * @return the new score after adding two or -1 if team name doesn't exist
     */
    private int addTwo (String teamName) {
        for (int n = 0 ; n < teams.size() ; n ++) {
            if (teamName.equalsIgnoreCase(teams.get(n))) {
                scores.set(n, scores.get(n) + 2);
                return scores.get(n);
            }
        }
        return -1;
    }

    /**
     * Adds three points to a given team and returns their score
     * @param teamName the team to add to
     * @return the new score after adding three or -1 if team name doesn't exist
     */
    private int addThree (String teamName) {
        for (int n = 0 ; n < teams.size() ; n ++) {
            if (teamName.equalsIgnoreCase(teams.get(n))) {
                scores.set(n, scores.get(n) + 3);
                return scores.get(n);
            }
        }
        return -1;
    }

    /**
     * Resets the score for a given team to 0
     * @param teamName the team name
     */
    private void resetScore (String teamName) {
        int index = teams.indexOf(teamName);
        scores.set(index, 0);
    }


    /**
     * Gets the score of the given team
     * @param teamName the team to get the score of
     * @return the score of the given team
     */
    private int getScore (String teamName) {
        for (int n = 0 ; n < teams.size() ; n ++) {
            if (teamName.equalsIgnoreCase(teams.get(n))) {
                return scores.get(n);
            }
        }
        return -1;
    }

}
