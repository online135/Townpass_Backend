"# hackathon_practice_spring_boot" 


maven 安裝
https://ithelp.ithome.com.tw/articles/10260305

以下一次就可以
gcloud init

gcloud projects add-iam-policy-binding kinetic-horizon-433215-u6 --member=serviceAccount:954251470184-compute@developer.gserviceaccount.com --role=roles/cloudbuild.builds.builder


每次

先執行
mvn install -DskipTests

然後
gcloud run deploy
