Azure Machine Learning WebService Client in Java
===============================================

#Description  
Azure Machine Learning のWebServiceにアクセスするjava projectです。  
  
#Usage  
configフォルダ内のparam.jsonファイルで設定を行います  
* url : WebServiceにアクセスするためのurl  
* api_key : WebServiceのAPI key  
* MorphoLogical : 形態素解析を行いたい列のカラム名を指定(配列形式)  
* StopWords : 形態素解析結果に含めない品詞を指定(形態素解析はkuromojiを使用)  
  
#execute  
java AmlClient 【CSVデータファイル】