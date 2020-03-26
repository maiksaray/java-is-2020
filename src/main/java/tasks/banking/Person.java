package tasks.banking;

import org.jetbrains.annotations.NotNull;

public class Person implements Comparable {
    private final String firstName;
    private final String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            var otherPerson = (Person) obj;
            return firstName.equals(otherPerson.getFirstName()) &&
                    lastName.equals(otherPerson.getLastName());
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof Person) {
            var otherPerson = (Person) o;
            var lastNameComparison = lastName.compareTo(otherPerson.getLastName());
            if (lastNameComparison != 0) {
                return lastNameComparison;
            } else {
                return firstName.compareTo(otherPerson.getFirstName());
            }
        }
        return 0;
    }
}
