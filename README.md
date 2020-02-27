# JMFramework
JMFramework

How to use:
1. make your project repository at git
2. create your project (new project) using android studio
3. on android studio VCS menu, import into version control, create git repository (make sure you are logged in using your git account)
4. from the bottom version control menu, add all the unversion files to the VCS (right click, add to VCS)
5. from android studio project terminal add git sub-module (git submodule add [this url])
6. add root (on your project settings.gradle add [include ':app',':JMFramework'])
7. on build.gradle(Module:app) add dependencies [implementation project(path: ':JMFramework')]
