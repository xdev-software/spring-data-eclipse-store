/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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
package software.xdev.spring.data.eclipse.store.repository.support.copier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DataTypeUtilTest
{
	@SuppressWarnings("ConstantValue")
	@Test
	void isObjectArray()
	{
		Assertions.assertTrue(DataTypeUtil.isObjectArray(new Object[]{}));
		Assertions.assertTrue(DataTypeUtil.isObjectArray(new Object[]{new Object()}));
		Assertions.assertTrue(DataTypeUtil.isObjectArray(new Object[]{new Object(), new Object()}));
		Assertions.assertTrue(DataTypeUtil.isObjectArray(new Object[]{null}));
		Assertions.assertTrue(DataTypeUtil.isObjectArray(new Object[]{null, new Object()}));
		Assertions.assertTrue(DataTypeUtil.isObjectArray(new String[]{"test"}));
		
		Assertions.assertFalse(DataTypeUtil.isObjectArray(null));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new int[]{}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new int[]{1}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new int[]{1, 2}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new Object()));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new long[]{1}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new double[]{1}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new byte[]{1}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new char[]{'c'}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new float[]{1}));
		Assertions.assertFalse(DataTypeUtil.isObjectArray(new short[]{1}));
	}
	
	@SuppressWarnings("ConstantValue")
	@Test
	void isPrimitiveArray()
	{
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new int[]{}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new int[]{1}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new int[]{1, 2}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new long[]{1}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new double[]{1}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new byte[]{1}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new char[]{'c'}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new float[]{1}));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveArray(new short[]{1}));
		
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(null));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(1));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(1L));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(1.0));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray('c'));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(1f));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray("test"));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(new Object()));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(new Object[]{}));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(new Object[]{new Object()}));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(new Object[]{new Object(), new Object()}));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(new Object[]{null}));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(new Object[]{null, new Object()}));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveArray(new String[]{"test"}));
	}
	
	@SuppressWarnings("DataFlowIssue")
	@Test
	void isPrimitiveType()
	{
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Integer.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Byte.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Character.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Boolean.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Double.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Float.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Long.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Short.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(String.class));
		Assertions.assertTrue(DataTypeUtil.isPrimitiveType(Void.class));
		
		Assertions.assertFalse(DataTypeUtil.isPrimitiveType(Object.class));
		Assertions.assertFalse(DataTypeUtil.isPrimitiveType(Object[].class));
		
		Assertions.assertThrows(NullPointerException.class, () -> DataTypeUtil.isPrimitiveType(null));
	}
}
