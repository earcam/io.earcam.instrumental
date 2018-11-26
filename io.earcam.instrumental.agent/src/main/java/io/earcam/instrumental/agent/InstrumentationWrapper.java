/*-
 * #%L
 * io.earcam.instrumental.agent
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
package io.earcam.instrumental.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

/**
 * <p>
 * InstrumentationWrapper class.
 * </p>
 *
 */
public class InstrumentationWrapper implements Instrumentation {

	private final AtomicReference<Instrumentation> delegate = new AtomicReference<>();


	/**
	 * <p>
	 * Setter for the field <code>delegate</code>.
	 * </p>
	 *
	 * @param delegate a {@link java.lang.instrument.Instrumentation} object.
	 */
	protected void setDelegate(Instrumentation delegate)
	{
		this.delegate.set(delegate);
	}


	/**
	 * <p>
	 * Getter for the field <code>delegate</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.instrument.Instrumentation} object.
	 */
	protected Instrumentation getDelegate()
	{
		return delegate.get();
	}


	@Override
	public void addTransformer(ClassFileTransformer transformer, boolean canRetransform)
	{
		delegate.get().addTransformer(transformer, canRetransform);
	}


	@Override
	public void addTransformer(ClassFileTransformer transformer)
	{
		delegate.get().addTransformer(transformer);
	}


	@Override
	public boolean removeTransformer(ClassFileTransformer transformer)
	{
		return delegate.get().removeTransformer(transformer);
	}


	@Override
	public boolean isRetransformClassesSupported()
	{
		return delegate.get().isRetransformClassesSupported();
	}


	@Override
	public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException
	{
		delegate.get().retransformClasses(classes);
	}


	@Override
	public boolean isRedefineClassesSupported()
	{
		return delegate.get().isRedefineClassesSupported();
	}


	@Override
	public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException
	{
		delegate.get().redefineClasses(definitions);
	}


	@Override
	public boolean isModifiableClass(Class<?> theClass)
	{
		return delegate.get().isModifiableClass(theClass);
	}


	@Override
	public Class<?>[] getAllLoadedClasses()
	{
		return delegate.get().getAllLoadedClasses();
	}


	@Override
	public Class<?>[] getInitiatedClasses(ClassLoader loader)
	{
		return delegate.get().getInitiatedClasses(loader);
	}


	@Override
	public long getObjectSize(Object objectToSize)
	{
		return delegate.get().getObjectSize(objectToSize);
	}


	@Override
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile)
	{
		delegate.get().appendToBootstrapClassLoaderSearch(jarfile);
	}


	@Override
	public void appendToSystemClassLoaderSearch(JarFile jarfile)
	{
		delegate.get().appendToSystemClassLoaderSearch(jarfile);
	}


	@Override
	public boolean isNativeMethodPrefixSupported()
	{
		return delegate.get().isNativeMethodPrefixSupported();
	}


	@Override
	public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix)
	{
		delegate.get().setNativeMethodPrefix(transformer, prefix);
	}

}
