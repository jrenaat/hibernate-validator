/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.validator.ap.record;

import java.util.Date;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @author Jan Schatteman
 */
public record RecordWithInvalidMethodConstraints(@NotBlank String string, @FutureOrPresent Date date) {

	public void doNothing(@Positive String s) {
		//
	}
}
