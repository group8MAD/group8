package it.polito.mad.group8


class Chat() {
    var chatName: String? = null
    var contactNickname: String? = null
    var contactUid: String? = null
    var notRead:Long? = null
    var lastMessage:Long? = null

    init {
        this.chatName = ""
        this.contactNickname = ""
        this.contactUid = ""
        this.notRead = 0
        this.lastMessage = 0
    }

}