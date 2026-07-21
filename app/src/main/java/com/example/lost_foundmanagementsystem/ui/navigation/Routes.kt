package com.example.lost_foundmanagementsystem.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val REPORT_LOST = "report_lost"
    const val REPORT_FOUND = "report_found"
    const val ITEMS = "items"
    const val ITEM_DETAIL = "item_detail/{type}/{id}"
    const val CLAIM_VERIFY = "claim_verify/{foundItemId}"
    const val CLAIMS = "claims"
    const val PROFILE = "profile"

    fun itemDetail(type: String, id: Int) = "item_detail/$type/$id"
    fun claimVerify(foundItemId: Int) = "claim_verify/$foundItemId"

    const val TYPE_LOST = "lost"
    const val TYPE_FOUND = "found"
}
