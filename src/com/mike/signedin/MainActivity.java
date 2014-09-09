package com.mike.signedin;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mike.appmodel.Task;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity {

	private ListView mListView;
	private EditText mTaskInput;
	private Button Submit_Button;
	private TaskListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

		initParse();

		Submit_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTaskInput.getText().length() > 0) {
					Task t = new Task();
					t.setACL(new ParseACL(ParseUser.getCurrentUser()));
					t.setUser(ParseUser.getCurrentUser());
					t.setDescription(mTaskInput.getText().toString());
					t.setCompleted(false);
					t.saveEventually();
					Toast.makeText(MainActivity.this, " Task Saved",
							Toast.LENGTH_LONG).show();
					mAdapter.insert(t, 0);
					mTaskInput.setText("");
				}
			}
		});

		mAdapter = new TaskListAdapter(this, new ArrayList<Task>());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new ItemClickListener());
		updateData();

	}

	private void init() {

		mTaskInput = (EditText) findViewById(R.id.task_input);
		mListView = (ListView) findViewById(R.id.task_list);
		Submit_Button = (Button) findViewById(R.id.submit_button);
	}

	private void initParse() {

		// (context,app_key,client_key)
		Parse.initialize(this, getResources().getString(R.string.app_id),
				getResources().getString(R.string.client_id));

		ParseAnalytics.trackAppOpened(getIntent());
		ParseObject.registerSubclass(Task.class);
		ParseUser currentUser = ParseUser.getCurrentUser();

		if (currentUser == null) {

			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();

		}

	}

	public void updateData() {
		ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback<Task>() {

			@Override
			public void done(List<Task> tasks, ParseException error) {
				if (tasks != null) {
					mAdapter.clear();
					mAdapter.addAll(tasks);

					// for (int i = 0; i < tasks.size(); i++) {
					// mAdapter.add(tasks.get(i));
					// }
				}
			}
		});
	}

	public class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Task task = mAdapter.getItem(position);
			TextView taskDescription = (TextView) view
					.findViewById(R.id.task_description);
			TextView taskResult = (TextView) view
					.findViewById(R.id.task_result);

			task.setCompleted(!task.isCompleted());

			if (task.isCompleted()) {
				taskDescription.setPaintFlags(taskDescription.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);

				taskResult.setText(getResources().getString(
						R.string.task_result_checked));
			} else {
				taskDescription.setPaintFlags(taskDescription.getPaintFlags()
						& (~Paint.STRIKE_THRU_TEXT_FLAG));
				taskResult.setText(getResources().getString(
						R.string.task_result));
			}

			task.saveEventually();

		}

	}

	private void LogOut() {

		ParseUser.logOut();
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
		Toast.makeText(MainActivity.this, "Logged Out!", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:

			break;

		case R.id.logout_id:

			LogOut();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}
