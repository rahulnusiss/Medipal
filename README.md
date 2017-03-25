# A quick guide for github pull request

## Clone to local

* Run `git clone https://github.com/huazhihao/MediPal` locally

## Submit a new change

* Before you start, run `git checkout -b FEATURE_BRANCH` to switch to a feature branch
* Work on your code
* Git add and commit
* Push this feature branch to remote by running `git push origin FEATURE_BRANCH`. Then you should be able to see the branch on https://github.com/huazhihao/MediPal/tree/FEATURE_BRANCH
* Create a pull request following https://help.github.com/articles/creating-a-pull-request/ to the master branch of https://github.com/huazhihao/MediPal/
* Wait for review

## Revise a change

* Make sure you are on a right local branch
* Modify your code
* Git add
* Run `git commit --amend` to modify the commit
* Run `git push origin FEATURE_BRANCH -f`
* Check if your change can reflect on the same web page of the pull request

## Sync your local repo when you have a conflict

* `git rebase origin/master`

# Work division

* Health/Medical history: Trung
* Medication & Categories: PP & Hua
* Consumption: Rahul
* Measurement: Hua
* ICE: Jack
* Appointment: Gong

# How to add a new page

## Create a f06.medipal.model class following class f06.medipal.model.Measurement, make sure every column is annotated like below:

	@Column(type = Column.INT)
	@Column(type = Column.DOUBLE)
	@Column(type = Column.DATETIME)
	@Column(type = Column.VARCHAR, length = 255)

You can use Android Studio to generate a constructor. It will be safer than human input.

## Add create table and drop table sql generator as below

Add

	db.execSQL(createTableSQL(NEW_MODEL_CLASS.class));

to `DataBaseHelper.onCreate()`

	db.execSQL(String.format(DROP_TEMPLATE, NEW_MODEL_CLASS.class.getSimpleName()));

to `DataBaseHelper.onUpgrade()`

This way is to minimize the effort of DAO, since the example provided by the teacher is too redundant. And more importantly, it is safer.

## Add a new fragment (maybe list)

And add it to class TabPagerAdapter's getItem() function, like this way:

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case Fragments.Measurement:
				return new MeasurementFragment();
			default:
				return new Fragment();
		}

	}

## Program your logic in the new created fragment  using generic DAO

	DBDAO<Measurement> f06.medipal.dao = new DBDAO<Measurement>(getBaseContext(), Measurement.class);
	Measurement item = new Measurement(
			bundle.getInt("Systolic"),
			bundle.getInt("Diastolic"),
			bundle.getInt("Pulse"),
			bundle.getDouble("Temperature"),
			bundle.getInt("Weight"),
			new Date()
	);
	f06.medipal.dao.save(item);

## (Optional) Add a new activity for accepting user input to create new record

In MainActivity there is a function like below, start your new activity this way so you can reuse the add button

	public void onbtnAddClick(View view) {
		final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerMain);
		int i = viewPager.getCurrentItem();
		switch (i) {
			case Fragments.Measurement:
				Intent intent = new Intent(this, NewMeasurementActivity.class);
				startActivityForResult(intent, Fragments.Measurement);
		}
	}

And save the user input to db by receiving parameters like below

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Fragments.Measurement) {
			if(resultCode == Activity.RESULT_OK){
				Bundle bundle = data.getBundleExtra("measurement");
				DBDAO<Measurement> f06.medipal.dao = new DBDAO<Measurement>(getBaseContext(), Measurement.class);
				Measurement item = new Measurement(
						bundle.getInt("Systolic"),
						bundle.getInt("Diastolic"),
						bundle.getInt("Pulse"),
						bundle.getDouble("Temperature"),
						bundle.getInt("Weight"),
						new Date()
				);
				f06.medipal.dao.save(item);
				final ViewPager viewPager = (ViewPager)findViewById(R.id.pagerMain);
				final TabPagerAdapter adapter = (TabPagerAdapter)viewPager.getAdapter();
				// Refresh all fragments
				adapter.notifyDataSetChanged();
			}
		}
	}

# Database file

Database file will be located as `/data/data/f06.medipal/databases/medipal.db`. Make sure you have installed adb so you can login into the Android operating system.

	# switch to root user
	adb root

	# fetch the db file(s) to local
	adb pull /data/data/f06.medipal/databases/medipal.db

	# login into Android shell
	adb shell

	# remove db files so DataBaseHelper will invoke onCreate() to create a new one
	rm /data/data/f06.medipal/databases/*

# Datetime Picker

There are three fragments in the `utils` folder: `DatePickerFragment`, `TimePickerFragment` and `SoloDatePickerFragment`. Here is a guide for the usage for these fragments.

1. Pick date only.
Use `SoloDatePickerFragment` and implement `OnDatePickedListener` in your activities for receiving the date which was picked by user.

2. Pick time only.
Use `TimePickerFragment` and implement `OnTimePickedListener` in your activities for receiving the time which was picked by user.
There is a `setDate` method in `TimePickerFragment` for you to set the date, if you don't invoke this method when you create the `TimePickerFragment`, the date will be the current date.

3. Pick date and time.
Use `DatePickerFragment` and implement `OnTimePickedListener` in your activities for receiving the date and time which was picked by user.
The `DatePickerFragment` will create `TimePickerFragment` for you automatically and the date and time will deliver through the `OnTimePickedListener` after the user picks the time.

Here is an example of time picker, and it's same for the other two pickers.
```java

public class NewAppointmentActivity extends AppCompatActivity implements TimePickerFragment.OnTimePickedListener{

	private TextView mDatetimeDisplay;
	public static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);

	@override
	public void onTimePicked(Date pickedDatetime) {
		// Receive the result and show it in text view.
        	mDatetimeDisplay.setText(formatter.format(pickedDatetime));
	}

	private void showTimePicker() {
		TimePickerFragment timePicker = new TimePickerFragment();
		timePicker.show(getSupportFragmentManager(), "time_picker");
		// Set date for time picker
		timePicker.setDate(mPickedDate);
	}
}
```

# Screenshots

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_Welcome.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_Personal.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_Menu.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_HealthBio_Create.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_HealthBio_List.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_NewMeasurement.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_Measurement.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_ICE_CREATE.png?raw=true" width="300"/>

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_ICE_DETAILS.png?raw=true" width="300">

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_ICE.png?raw=true" width="300">

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_Medicine.png?raw=true" width="300">

<img src="https://github.com/huazhihao/MediPal/blob/master/Screenshot_Consumption.png?raw=true" width="300">