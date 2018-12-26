
/*-
 * #%L
 * io.earcam.instrumental.module.auto
 * %%
 * Copyright (C) 2018 earcam
 * %%
 * SPDX-License-Identifier: (BSD-3-Clause OR EPL-1.0 OR Apache-2.0 OR MIT)
 * 
 * You <b>must</b> choose to accept, in full - any individual or combination of 
 * the following licenses:
 * <ul>
 * 	<li><a href="https://opensource.org/licenses/BSD-3-Clause">BSD-3-Clause</a></li>
 * 	<li><a href="https://www.eclipse.org/legal/epl-v10.html">EPL-1.0</a></li>
 * 	<li><a href="https://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a></li>
 * 	<li><a href="https://opensource.org/licenses/MIT">MIT</a></li>
 * </ul>
 * #L%
 */
import static io.earcam.instrumental.module.auto.Reader.reader;
import static io.earcam.instrumental.module.auto.ReaderTest.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.earcam.instrumental.module.auto.Reader;
import io.earcam.instrumental.reflect.Resources;

public class NoPackageReaderTest {

	@Test
	void handlesNoPackageTypes()
	{
		Map<String, Set<String>> imports = new HashMap<>();

		reader()
				.addImportListener(imports::put)
				.processClass(Resources.classAsBytes(DependsOnNoPackage.class));

		assertThat(imports, is(aMapWithSize(1)));

		assertThat(imports, hasEntry(equalTo(cn(DependsOnNoPackage.class)), containsInAnyOrder(
				cn(Object.class),
				cn(NoPackage.class))));
	}


	@Test
	void handlesNoPackageTypesWithReducedImports()
	{
		Map<String, Set<String>> imports = new HashMap<>();

		reader()
				.addImportListener(imports::put)
				.setImportedTypeReducer(Reader::typeToPackageReducer)
				.processClass(Resources.classAsBytes(DependsOnNoPackage.class));

		assertThat(imports, is(aMapWithSize(1)));

		assertThat(imports, hasEntry(equalTo(cn(DependsOnNoPackage.class)), containsInAnyOrder(
				pkg(Object.class),
				"")));
	}
}
