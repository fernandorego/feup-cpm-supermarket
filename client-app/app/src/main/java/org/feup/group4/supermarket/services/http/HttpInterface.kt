package org.feup.group4.supermarket.services.http

interface HttpInterface {
    fun onPreExecute()
    fun onPostExecute(json:String)
    fun onErrorExecute(code:Int, msg:String)
}