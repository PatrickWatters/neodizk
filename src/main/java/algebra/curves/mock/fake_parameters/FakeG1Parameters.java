/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock.fake_parameters;

import algebra.curves.mock.FakeG1;
import algebra.curves.mock.abstract_fake_parameters.AbstractFakeG1Parameters;
import algebra.fields.mock.fieldparameters.LargeFpParameters;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class FakeG1Parameters extends AbstractFakeG1Parameters implements Serializable {

  private LargeFpParameters FqParameters;

  private FakeG1 ZERO;
  private FakeG1 ONE;

  private ArrayList<Integer> fixedBaseWindowTable;

  public LargeFpParameters FqParameters() {
    if (FqParameters == null) {
      FqParameters = new LargeFpParameters();
    }

    return FqParameters;
  }

  public FakeG1 ZERO() {
    if (ZERO == null) {
      ZERO = new FakeG1(BigInteger.ZERO, this);
    }

    return ZERO;
  }

  public FakeG1 ONE() {
    if (ONE == null) {
      ONE = new FakeG1(BigInteger.ONE, this);
    }

    return ONE;
  }

  public ArrayList<Integer> fixedBaseWindowTable() {
    if (fixedBaseWindowTable == null) {
      fixedBaseWindowTable = new ArrayList<>(0);
    }

    return fixedBaseWindowTable;
  }
}
