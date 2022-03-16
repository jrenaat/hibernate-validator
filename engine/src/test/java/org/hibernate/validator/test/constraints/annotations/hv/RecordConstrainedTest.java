/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.test.constraints.annotations.hv;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.hibernate.validator.test.constraints.annotations.AbstractConstrainedTest;
import org.hibernate.validator.testutils.ValidatorUtil;

import org.testng.annotations.Test;

import static org.hibernate.validator.testutil.ConstraintViolationAssert.assertNoViolations;
import static org.hibernate.validator.testutil.ConstraintViolationAssert.assertThat;
import static org.hibernate.validator.testutil.ConstraintViolationAssert.violationOf;

/**
 *
 */
public class RecordConstrainedTest extends AbstractConstrainedTest {

	/*
	* tests
	* explicit canonical constructor
	* compact constructor
	*/

	@Test
	public void testName() {
		PersonRecord r0 = new PersonRecord( "David", 15 );
		Set<ConstraintViolation<PersonRecord>> violations = validator.validate( r0 );
		assertNoViolations( violations );

		PersonRecord r1 = new PersonRecord( null, 15 );
		violations = validator.validate( r1 );
		assertThat( violations ).containsOnlyViolations( violationOf( NotBlank.class ).withProperty( "name" ) );
		assertThat( violations ).containsOnlyViolations( violationOf( NotBlank.class ).withMessage( "Name cannot be null or empty" ) );

		PersonRecord r2 = new PersonRecord( "", 15 );
		violations = validator.validate( r2 );
		assertThat( violations ).containsOnlyViolations( violationOf( NotBlank.class ).withProperty( "name" ) );
		assertThat( violations ).containsOnlyViolations( violationOf( NotBlank.class ).withMessage( "Name cannot be null or empty" ) );

		PersonRecord r3 = new PersonRecord( " ", 15 );
		violations = validator.validate( r3 );
		assertThat( violations ).containsOnlyViolations( violationOf( NotBlank.class ).withProperty( "name" ) );
		assertThat( violations ).containsOnlyViolations( violationOf( NotBlank.class ).withMessage( "Name cannot be null or empty" ) );
	}

	@Test
	public void testAge() {
		PersonRecord r0 = new PersonRecord( "David", 15 );
		Set<ConstraintViolation<PersonRecord>> violations = validator.validate( r0 );
		assertNoViolations( violations );

		PersonRecord r1 = new PersonRecord( "David", 0 );
		violations = validator.validate( r1 );
		assertThat( violations ).containsOnlyViolations( violationOf( Positive.class ).withProperty( "age" ) );
		assertThat( violations ).containsOnlyViolations( violationOf( Positive.class ).withMessage( "Age has to be a strictly positive integer" ) );

		PersonRecord r2 = new PersonRecord( "David", -15 );
		violations = validator.validate( r2 );
		assertThat( violations ).containsOnlyViolations( violationOf( Positive.class ).withProperty( "age" ) );
		assertThat( violations ).containsOnlyViolations( violationOf( Positive.class ).withMessage( "Age has to be a strictly positive integer" ) );
	}

	@Test
	public void explicitConstructorTestName() {
	}

	record PersonRecord(@NotBlank(message = "Name cannot be null or empty") String name, @Positive(message = "Age has to be a strictly positive integer") int age) {
	}

	private record ConstructorValidationRecord(String name, int age) implements ExplicitConstructorValidator {
		private ConstructorValidationRecord(@NotBlank(message = "Name cannot be null or empty") String name, @Positive(message = "Age has to be a strictly positive integer") int age) {
			validate( name, age );
			this.name = name;
			this.age = age;
		}
	}

	private interface ExplicitConstructorValidator {
		default void validate(Object ... args) {
			Validator v = ValidatorUtil.getValidator();
			Constructor c = getClass().getDeclaredConstructors()[0];
			Set<ConstraintViolation<?>> violations = v.forExecutables().validateConstructorParameters( c, args );
			if ( !violations.isEmpty() ) {
				String message = violations.stream()
						.map( ConstraintViolation::getMessage )
						.collect( Collectors.joining( ";" ) );
				throw new ConstraintViolationException( message, violations );
			}
		}
	}
}

