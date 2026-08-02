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
package software.xdev.spring.data.eclipse.store.repository.support.copier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DataTypeDetectorTest
{
	@SuppressWarnings("ConstantValue")
	@Test
	void isObjectArray()
	{
		Assertions.assertTrue(DataTypeDetector.isObjectArray(new Object[]{}));
		Assertions.assertTrue(DataTypeDetector.isObjectArray(new Object[]{new Object()}));
		Assertions.assertTrue(DataTypeDetector.isObjectArray(new Object[]{new Object(), new Object()}));
		Assertions.assertTrue(DataTypeDetector.isObjectArray(new Object[]{null}));
		Assertions.assertTrue(DataTypeDetector.isObjectArray(new Object[]{null, new Object()}));
		Assertions.assertTrue(DataTypeDetector.isObjectArray(new String[]{"test"}));
		
		Assertions.assertFalse(DataTypeDetector.isObjectArray(null));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new int[]{}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new int[]{1}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new int[]{1, 2}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new Object()));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new long[]{1}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new double[]{1}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new byte[]{1}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new char[]{'c'}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new float[]{1}));
		Assertions.assertFalse(DataTypeDetector.isObjectArray(new short[]{1}));
	}
	
	@SuppressWarnings("ConstantValue")
	@Test
	void isPrimitiveArray()
	{
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new int[]{}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new int[]{1}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new int[]{1, 2}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new long[]{1}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new double[]{1}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new byte[]{1}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new char[]{'c'}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new float[]{1}));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveArray(new short[]{1}));
		
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(null));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(1));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(1L));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(1.0));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray('c'));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(1f));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray("test"));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(new Object()));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(new Object[]{}));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(new Object[]{new Object()}));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(new Object[]{new Object(), new Object()}));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(new Object[]{null}));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(new Object[]{null, new Object()}));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveArray(new String[]{"test"}));
	}
	
	@SuppressWarnings("DataFlowIssue")
	@Test
	void isPrimitiveType()
	{
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Integer.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Byte.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Character.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Boolean.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Double.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Float.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Long.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Short.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(String.class));
		Assertions.assertTrue(DataTypeDetector.isPrimitiveType(Void.class));
		
		Assertions.assertFalse(DataTypeDetector.isPrimitiveType(Object.class));
		Assertions.assertFalse(DataTypeDetector.isPrimitiveType(Object[].class));
		
		Assertions.assertThrows(NullPointerException.class, () -> DataTypeDetector.isPrimitiveType(null));
	}
}
