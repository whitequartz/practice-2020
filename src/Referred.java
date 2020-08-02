enum ReferredType {
	ANOTHER,
	VARIABLE,
	FUNCTION,
}

abstract class Referred {
	abstract String getName();
	abstract ReferredType getType();
}

// Класс объявленных переменных
class Variable extends Referred {
	private String name;
	private int value;

	Variable(String name, int value) {
		this.name = name;
		this.value = value;
	}

	int getValue() {
		return this.value;
	}

	void setValue(int value) {
		this.value = value;
	}

	@Override
	String getName() {
		return this.name;
	}

	@Override
	ReferredType getType() {
		return ReferredType.VARIABLE;
	}
}

// Класс объявленных функций
class Function extends Referred {
	private String name;
	private Cursor cursor;

	public Function(String name, Cursor cursor) {
		this.name = name;
		this.cursor = cursor.copy();
	}

	Cursor getCursor() {
		return this.cursor.copy();
	}

	@Override
	String getName() {
		return this.name;
	}

	@Override
	ReferredType getType() {
		return ReferredType.FUNCTION;
	}
}
