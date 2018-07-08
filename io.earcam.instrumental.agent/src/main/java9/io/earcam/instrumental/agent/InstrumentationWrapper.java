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


import java.lang.Module;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>InstrumentationWrapper class.</p>
 *
 */
public class InstrumentationWrapper implements Instrumentation {

	private volatile Instrumentation delegate;

	
	/**
	 * Why o why couldn't this have just been a default method returning {@code false}? 
	 * To <b>force</b> somebody, anybody, into using JEP-238 mutli-release JARs?
	 * @param module
	 * @return {@code true} IFF the module is modifiable
	 */
	public boolean isModifiableModule(Module module)
	{
		return delegate.isModifiableModule(module);
	}

    public void redefineModule(Module module,
            Set<Module> extraReads,
            Map<String, Set<Module>> extraExports,
            Map<String, Set<Module>> extraOpens,
            Set<Class<?>> extraUses,
            Map<Class<?>, List<Class<?>>> extraProvides)
    {
    	delegate.redefineModule(module,
                extraReads,
                extraExports,
                extraOpens,
                extraUses,
                extraProvides);
    }


    /**
	 * <p>Setter for the field <code>delegate</code>.</p>
	 *
	 * @param delegate a {@link java.lang.instrument.Instrumentation} object.
	 */
	protected void setDelegate(Instrumentation delegate)
	{
		this.delegate = delegate;
	}


	/**
	 * <p>Getter for the field <code>delegate</code>.</p>
	 *
	 * @return a {@link java.lang.instrument.Instrumentation} object.
	 */
	protected Instrumentation getDelegate()
	{
		return delegate;
	}


	/** {@inheritDoc} */
	@Override
	public void addTransformer(ClassFileTransformer transformer, boolean canRetransform)
	{
		delegate.addTransformer(transformer, canRetransform);
	}


	/** {@inheritDoc} */
	@Override
	public void addTransformer(ClassFileTransformer transformer)
	{
		delegate.addTransformer(transformer);
	}


	/** {@inheritDoc} */
	@Override
	public boolean removeTransformer(ClassFileTransformer transformer)
	{
		return delegate.removeTransformer(transformer);
	}


	/** {@inheritDoc} */
	@Override
	public boolean isRetransformClassesSupported()
	{
		return delegate.isRetransformClassesSupported();
	}


	/** {@inheritDoc} */
	@Override
	public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException
	{
		delegate.retransformClasses(classes);
	}


	/** {@inheritDoc} */
	@Override
	public boolean isRedefineClassesSupported()
	{
		return delegate.isRedefineClassesSupported();
	}


	/** {@inheritDoc} */
	@Override
	public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException
	{
		delegate.redefineClasses(definitions);
	}


	/** {@inheritDoc} */
	@Override
	public boolean isModifiableClass(Class<?> theClass)
	{
		return delegate.isModifiableClass(theClass);
	}


	/** {@inheritDoc} */
	@Override
	public Class<?>[] getAllLoadedClasses()
	{
		return delegate.getAllLoadedClasses();
	}


	/** {@inheritDoc} */
	@Override
	public Class<?>[] getInitiatedClasses(ClassLoader loader)
	{
		return delegate.getInitiatedClasses(loader);
	}


	/** {@inheritDoc} */
	@Override
	public long getObjectSize(Object objectToSize)
	{
		return delegate.getObjectSize(objectToSize);
	}


	/** {@inheritDoc} */
	@Override
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile)
	{
		delegate.appendToBootstrapClassLoaderSearch(jarfile);
	}


	/** {@inheritDoc} */
	@Override
	public void appendToSystemClassLoaderSearch(JarFile jarfile)
	{
		delegate.appendToSystemClassLoaderSearch(jarfile);
	}


	/** {@inheritDoc} */
	@Override
	public boolean isNativeMethodPrefixSupported()
	{
		return delegate.isNativeMethodPrefixSupported();
	}


	/** {@inheritDoc} */
	@Override
	public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix)
	{
		delegate.setNativeMethodPrefix(transformer, prefix);
	}

}
