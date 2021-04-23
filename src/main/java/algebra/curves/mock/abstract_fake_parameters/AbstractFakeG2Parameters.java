/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package algebra.curves.mock.abstract_fake_parameters;

import algebra.curves.mock.FakeG2;
import algebra.fields.mock.fieldparameters.LargeFpParameters;
import java.util.ArrayList;

public abstract class AbstractFakeG2Parameters {

  public abstract LargeFpParameters FqParameters();

  public abstract FakeG2 ZERO();

  public abstract FakeG2 ONE();

  public abstract ArrayList<Integer> fixedBaseWindowTable();
}
