git add . && git commit -m "release prepare" && git push
git checkout master
git merge develop
git add . && git commit -m "release prepare" && git push
mvn --batch-mode -s /Users/rm/Eclipse/m2/skjlls/settings-home.xml -Dresume=false release:prepare release:perform
git checkout develop
git merge master

