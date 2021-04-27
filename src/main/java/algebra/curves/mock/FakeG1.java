/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock;

import algebra.curves.AbstractG1;
import algebra.curves.mock.abstract_fake_parameters.AbstractFakeG1Parameters;
import algebra.fields.Fp;
import java.math.BigInteger;
import java.util.ArrayList;

public class FakeG1 extends AbstractG1<FakeG1> {

  protected final Fp element;
  protected AbstractFakeG1Parameters FakeG1Parameters;

  public FakeG1(final Fp element, final AbstractFakeG1Parameters FakeG1Parameters) {
    this.element = element;
    this.FakeG1Parameters = FakeG1Parameters;
  }

  public FakeG1(final BigInteger number, final AbstractFakeG1Parameters FakeG1Parameters) {
    this(new Fp(number, FakeG1Parameters.FqParameters()), FakeG1Parameters);
  }

  public FakeG1(final String number, final AbstractFakeG1Parameters FakeG1Parameters) {
    this(new Fp(number, FakeG1Parameters.FqParameters()), FakeG1Parameters);
  }

  public FakeG1(final long number, final AbstractFakeG1Parameters FakeG1Parameters) {
    this(new Fp(Long.toString(number), FakeG1Parameters.FqParameters()), FakeG1Parameters);
  }

  public FakeG1 self() {
    return this;
  }

  public FakeG1 add(final FakeG1 other) {
    return new FakeG1(this.element.add(other.element), FakeG1Parameters);
  }

  public FakeG1 sub(final FakeG1 other) {
    return new FakeG1(this.element.sub(other.element), FakeG1Parameters);
  }

  public FakeG1 zero() {
    return FakeG1Parameters.ZERO();
  }

  public boolean isZero() {
    return this.equals(FakeG1Parameters.ZERO());
  }

  public boolean isSpecial() {
    return isZero();
  }

  public FakeG1 one() {
    return FakeG1Parameters.ONE();
  }

  public boolean isOne() {
    return this.equals(FakeG1Parameters.ONE());
  }

  public FakeG1 random(final Long seed, final byte[] secureSeed) {
    return new FakeG1(element.random(seed, secureSeed), FakeG1Parameters);
  }

  public FakeG1 negate() {
    return new FakeG1(this.element.negate(), FakeG1Parameters);
  }

  public FakeG1 dbl() {
    return this.add(this);
  }

  public int bitSize() {
    return this.element.bitSize();
  }

  public FakeG1 toAffineCoordinates() {
    return this;
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    return FakeG1Parameters.fixedBaseWindowTable();
  }

  public BigInteger toBigInteger() {
    return this.element.toBigInteger();
  }

  public String toString() {
    return this.element.toString();
  }

  public boolean equals(final FakeG1 other) {
    if (other == null) {
      return false;
    }

    return this.element.equals(other.element);
  }
}
