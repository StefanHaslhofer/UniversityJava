package gradeTable.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {

	private final StringProperty id;
	private final StringProperty firstName;
	private final StringProperty lastName;
	private final IntegerProperty skz;

	public Student(String id, String firstName, String name, int sn) {
		this.id = new SimpleStringProperty(id);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(name);
		this.skz = new SimpleIntegerProperty(sn);
	}

	public String getId() {
		return id.get();
	}

	public StringProperty idProperty() {
		return id;
	}

	public String getFirstName() {
		return firstName.get();
	}

	public StringProperty firstNameProperty() {
		return firstName;
	}

	public String getLastName() {
		return lastName.get();
	}

	public StringProperty lastNameProperty() {
		return lastName;
	}

	public int getSkz() {
		return skz.get();
	}

	public IntegerProperty skzProperty() {
		return skz;
	}
}
