# kudigo-mobile-money-payment-util

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.kudigo:kudigo-mobile-money-payment-util:1.1'
	}
	
  Step 3. Add ApiKey to your project and pass it in your call.
