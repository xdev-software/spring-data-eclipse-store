/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.constraints;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;


public class ConstraintDaoObject
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@AssertFalse
	boolean isAlwaysFalse;
	
	@AssertTrue
	boolean isAlwaysTrue;
	
	@Digits(integer = 6, fraction = 2)
	BigDecimal price;
	
	@DecimalMin("5.00")
	BigDecimal discountMin5;
	
	@DecimalMax("30.00")
	BigDecimal discountMax20;
	
	@Email
	String emailField;
	
	@Future
	Date futureField;
	
	@FutureOrPresent
	Date futureOrPresentField;
	
	@Max(10)
	int quantityMax10;
	
	@Min(5)
	int quantityMin5;
	
	@Negative
	int negativeField;
	
	@NegativeOrZero
	int negativeOrZeroField;
	
	@NotBlank
	String messageNotBlank;
	
	@NotEmpty
	String messageNotEmpty;
	
	@NotNull
	String username;
	
	@Null
	String unusedString;
	
	@Past
	Date pastField;
	
	@PastOrPresent
	Date pastOrPresentField;
	
	@Pattern(regexp = "\\(\\d{3}\\)\\d{3}-\\d{4}")
	String phoneNumber;
	
	@Positive
	BigDecimal area;
	
	@PositiveOrZero
	int positiveOrZeroField;
	
	@Size(min = 2, max = 240)
	String messageMin2AndMax240;
	
	public ConstraintDaoObject()
	{
		this.isAlwaysFalse = false;
		this.isAlwaysTrue = true;
		this.price = BigDecimal.valueOf(123456.78);
		this.discountMin5 = BigDecimal.valueOf(5);
		this.discountMax20 = BigDecimal.valueOf(20);
		this.emailField = "some@email.com";
		this.futureField = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
		this.futureOrPresentField = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
		this.quantityMax10 = 10;
		this.quantityMin5 = 5;
		this.negativeField = -1;
		this.negativeOrZeroField = 0;
		this.messageNotBlank = "some message";
		this.messageNotEmpty = "some message";
		this.username = "";
		this.pastField = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
		this.pastOrPresentField = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
		this.phoneNumber = "(123)456-7890";
		this.area = BigDecimal.valueOf(1);
		this.positiveOrZeroField = 1;
		this.messageMin2AndMax240 = "..";
	}
	
	@AssertFalse
	public boolean isAlwaysFalse()
	{
		return this.isAlwaysFalse;
	}
	
	public void setAlwaysFalse(@AssertFalse final boolean alwaysFalse)
	{
		this.isAlwaysFalse = alwaysFalse;
	}
	
	@AssertTrue
	public boolean isAlwaysTrue()
	{
		return this.isAlwaysTrue;
	}
	
	public void setAlwaysTrue(@AssertTrue final boolean alwaysTrue)
	{
		this.isAlwaysTrue = alwaysTrue;
	}
	
	public @Digits(integer = 6, fraction = 2) BigDecimal getPrice()
	{
		return this.price;
	}
	
	public void setPrice(final @Digits(integer = 6, fraction = 2) BigDecimal price)
	{
		this.price = price;
	}
	
	public @DecimalMin("5.00") BigDecimal getDiscountMin5()
	{
		return this.discountMin5;
	}
	
	public void setDiscountMin5(final @DecimalMin("5.00") BigDecimal discountMin5)
	{
		this.discountMin5 = discountMin5;
	}
	
	public @DecimalMax("30.00") BigDecimal getDiscountMax20()
	{
		return this.discountMax20;
	}
	
	public void setDiscountMax20(final @DecimalMax("30.00") BigDecimal discountMax20)
	{
		this.discountMax20 = discountMax20;
	}
	
	public @Email String getEmailField()
	{
		return this.emailField;
	}
	
	public void setEmailField(final @Email String emailField)
	{
		this.emailField = emailField;
	}
	
	public @Future Date getFutureField()
	{
		return this.futureField;
	}
	
	public void setFutureField(final @Future Date futureField)
	{
		this.futureField = futureField;
	}
	
	public @FutureOrPresent Date getFutureOrPresentField()
	{
		return this.futureOrPresentField;
	}
	
	public void setFutureOrPresentField(final @FutureOrPresent Date futureOrPresentField)
	{
		this.futureOrPresentField = futureOrPresentField;
	}
	
	@Max(10)
	public int getQuantityMax10()
	{
		return this.quantityMax10;
	}
	
	public void setQuantityMax10(@Max(10) final int quantityMax10)
	{
		this.quantityMax10 = quantityMax10;
	}
	
	@Min(5)
	public int getQuantityMin5()
	{
		return this.quantityMin5;
	}
	
	public void setQuantityMin5(@Min(5) final int quantityMin5)
	{
		this.quantityMin5 = quantityMin5;
	}
	
	@Negative
	public int getNegativeField()
	{
		return this.negativeField;
	}
	
	public void setNegativeField(@Negative final int negativeField)
	{
		this.negativeField = negativeField;
	}
	
	@NegativeOrZero
	public int getNegativeOrZeroField()
	{
		return this.negativeOrZeroField;
	}
	
	public void setNegativeOrZeroField(@NegativeOrZero final int negativeOrZeroField)
	{
		this.negativeOrZeroField = negativeOrZeroField;
	}
	
	public @NotBlank String getMessageNotBlank()
	{
		return this.messageNotBlank;
	}
	
	public void setMessageNotBlank(final @NotBlank String messageNotBlank)
	{
		this.messageNotBlank = messageNotBlank;
	}
	
	public @NotEmpty String getMessageNotEmpty()
	{
		return this.messageNotEmpty;
	}
	
	public void setMessageNotEmpty(final @NotEmpty String messageNotEmpty)
	{
		this.messageNotEmpty = messageNotEmpty;
	}
	
	public @NotNull String getUsername()
	{
		return this.username;
	}
	
	public void setUsername(final @NotNull String username)
	{
		this.username = username;
	}
	
	public @Null String getUnusedString()
	{
		return this.unusedString;
	}
	
	public void setUnusedString(final @Null String unusedString)
	{
		this.unusedString = unusedString;
	}
	
	public @Past Date getPastField()
	{
		return this.pastField;
	}
	
	public void setPastField(final @Past Date pastField)
	{
		this.pastField = pastField;
	}
	
	public @PastOrPresent Date getPastOrPresentField()
	{
		return this.pastOrPresentField;
	}
	
	public void setPastOrPresentField(final @PastOrPresent Date pastOrPresentField)
	{
		this.pastOrPresentField = pastOrPresentField;
	}
	
	public @Pattern(regexp = "\\(\\d{3}\\)\\d{3}-\\d{4}") String getPhoneNumber()
	{
		return this.phoneNumber;
	}
	
	public void setPhoneNumber(final @Pattern(regexp = "\\(\\d{3}\\)\\d{3}-\\d{4}") String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
	
	public @Positive BigDecimal getArea()
	{
		return this.area;
	}
	
	public void setArea(final @Positive BigDecimal area)
	{
		this.area = area;
	}
	
	@PositiveOrZero
	public int getPositiveOrZeroField()
	{
		return this.positiveOrZeroField;
	}
	
	public void setPositiveOrZeroField(@PositiveOrZero final int positiveOrZeroField)
	{
		this.positiveOrZeroField = positiveOrZeroField;
	}
	
	public @Size(min = 2, max = 240) String getMessageMin2AndMax240()
	{
		return this.messageMin2AndMax240;
	}
	
	public void setMessageMin2AndMax240(final @Size(min = 2, max = 240) String messageMin2AndMax240)
	{
		this.messageMin2AndMax240 = messageMin2AndMax240;
	}
}
