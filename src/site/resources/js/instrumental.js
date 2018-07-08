/*-
 * #%L
 * io.earcam.instrumental
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
function pronounceOxford(oxfordMp3)
{
	pronounceUrl('//ssl.gstatic.com/dictionary/static/sounds/oxford/' + oxfordMp3);
}

function pronounceUrl(earl)
{
	var audio = document.createElement('audio');
	audio.src = earl;
	audio.preload = 'auto';
	audio.display = 'none';
	document.body.appendChild(audio);
	audio.play();

}