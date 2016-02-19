//package com.runner.sportsmeter.common;
//
///**
// * Created by AMR on 11.10.2015 ..
// */
//    public class JsonResponseHandler
////        implements ResponseHandler<List<String>> {
//        {
////        @Override
////        public List<String> handleResponse(HttpResponse response)
//////                throws ClientProtocolException, IOException
////        {
////            List<String> result = new ArrayList<String>();
//////            String JSONResponse = new BasicResponseHandler()
//////                    .handleResponse(response);
//////            try {
//////                // Get top-level JSON Object - a Map
////////                JSONObject responseObject = (JSONObject) new JSONTokener(
////////                        JSONResponse).nextValue();
//////
////////                String url = responseObject.get("profile_image_url").toString();
////////                result.add(url);
//////            } catch (JSONException e) {
//////                e.printStackTrace();
//////            }
////            return result;
////        }
//    }
