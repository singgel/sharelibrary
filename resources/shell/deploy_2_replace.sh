#!/bin/bash

# prepare variable
PRJ="{{PROJECT_NAME}}"
UNZIP_DIR="{{PROJECT_NAME}}"

mv /data/deploy/${PRJ}-bin.zip /data/deploy/${PRJ}.zip

# unzip
# init new version
cp -r /data/deploy /data/tools/deploy
rm -rf /data/tools/deploy/$PRJ
unzip -d /data/tools/deploy /data/tools/deploy/${PRJ}.zip
[ $UNZIP_DIR != $PRJ ] && mv /data/tools/deploy/$UNZIP_DIR /data/tools/deploy/$PRJ

# calculate diff
cd /data/deploy && for file in `find . -type f | grep -v -w logs`; do md5sum $file; done | sort > /data/tools/base.md5
cd /data/tools/deploy && for file in `find . -type f | grep -v -w logs`; do md5sum $file; done | sort > /data/tools/new.md5
diff /data/tools/base.md5 /data/tools/new.md5 > /data/tools/diff

# replace diff
for del_file in `grep '<' /data/tools/diff | awk -F' ./' '{print $2}'`; do
  echo rm `ls -lh /data/deploy/$del_file`
  rm -rf /data/deploy/$del_file;
done
for add_file in `grep '>' /data/tools/diff | awk -F' ./' '{print $2}'`; do
  mkdir -p `dirname /data/deploy/$add_file`
  mv /data/tools/deploy/$add_file /data/deploy/$add_file
  echo mv `ls -lh /data/deploy/$add_file`
done

# clear
rm -rf /data/tools/deploy
rm -rf /data/deploy/*.zip /data/tools/deploy/*.zip
rm -rf /data/tools/*.md5 /data/tools/diff

# set script
#ln -s /data/deploy/$PRJ-$curdate /data/deploy/$PRJ
chmod +x /data/deploy/$PRJ/bin/*.sh
chown app:app /data/deploy/$PRJ* -R

rm -rf /data/deploy/*.zip
chown app:app /data/tools -R
#echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC7jqza1EnrQtFVO6owCEG5AXXUKmsKWWTn1O3UU8YHuadIButbzQafcnzma4nyT40GCbgqNdCqNrcAGpzNps4RGNsybtdRmv9/I+iXkRgHeGOeZWHAiY+5SBvbeUyD9eRQpZObNP05rZmj3vepiCsAChkTE1/IZ9U9lTGW6bKYK18monf6OAVY4pkqZs2ZP9E+a1pIME/7N+Mdg27yvNhq1Lsk/a/VZV3pd38lDIq7yRnxddEkdO9uCUwpSu8tQfR+G13QDV+O4NqE6uYcsr8y89U/Ur1X+tpc6EWL9WWjxxLcgPExs6cCmDfDV3HMKzARU3fioqWkPVAn0p5KXakT jumpserver@bjxg-ap-25-11" >> /home/app/.ssh/authorized_keys