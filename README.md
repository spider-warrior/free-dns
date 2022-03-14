
参看文档
---

#### 报文结构
##### https://www.rfc-editor.org/rfc/rfc1035.txt

Format

All communications inside of the domain protocol are carried in a single
format called a message.  The top level format of message is divided
into 5 sections (some of which are empty in certain cases) shown below:

    +---------------------+
    |        Header       |
    +---------------------+
    |       Question      | the question for the name server
    +---------------------+
    |        Answer       | RRs answering the question
    +---------------------+
    |      Authority      | RRs pointing toward an authority
    +---------------------+
    |      Additional     | RRs holding additional information
    +---------------------+

The header section is always present.  The header includes fields that
specify which of the remaining sections are present, and also specify
whether the message is a query or a response, a standard query or some
other opcode, etc.

The names of the sections after the header are derived from their use in
standard queries.  The question section contains fields that describe a
question to a name server.  These fields are a query type (QTYPE), a
query class (QCLASS), and a query domain name (QNAME).  The last three
sections have the same format: a possibly empty list of concatenated
resource records (RRs).  The answer section contains RRs that answer the
question; the authority section contains RRs that point toward an
authoritative name server; the additional records section contains RRs
which relate to the query, but are not strictly answers for the
question.


#### 七大资源记录
##### https://blog.csdn.net/weixin_41545330/article/details/80865676

DNS分为正向查找区域和反向查找区域，然后在分为，主要，辅助，存根区域，在这些区域里，又存在着很多的记录:

1，A记录 A记录也称为主机记录，是使用最广泛的DNS记录，A记录的基本作用就是说明一个域名对应的IP是多少， 它是域名和IP地址的对应关系，表现形式为 www.contoso.com 192.168.1.1 这就是一个A记录！A记录除了进行域名IP对应以外，还有一个高级用法，可以作为低成本的负载均衡的解决方案，比如说，www.contoso.com 可以创建多个A记录，对应多台物理服务器的IP地址，可以实现基本的流量均衡！)

2，NS记录 NS记录和SOA记录是任何一个DNS区域都不可或缺的两条记录，NS记录也叫名称服务器记录，用于说明这个区域有哪些DNS服务器负责解析，SOA记录说明负责解析的DNS服务器中哪一个是主服务器。因此，任何一个DNS区域都不可能缺少这两条记录。NS记录，说明了在这个区域里，有多少个服务器来承担解析的任务

3，SOA记录 NS记录说明了有多台服务器在进行解析，但哪一个才是主服务器呢，NS并没有说明，这个就要看SOA记录了，SOA名叫起始授权机构记录，SOA记录说明了在众多NS记录里那一台才是主要的服务器！

4，MX记录 全称是邮件交换记录，在使用邮件服务器的时候，MX记录是无可或缺的，比如A用户向B用户发送一封邮件，那么他需要向ＤＮＳ查询Ｂ的MX记录，DNS在定位到了B的MX记录后反馈给A用户，然后Ａ用户把邮件投递到B用户的ＭＸ记录服务器里！

５，Cname记录 又叫别名记录，我们可以这么理解，我们小的时候都会有一个小名，长大了都是学名，那么正规来说学名的符合公安系统的，那个小名只是我们的一个代名词而已，这也存在一个好处，就是比暴漏自己，比如一个网站a.com 在发布的时候，他可以建立一个别名记录，把B.com发不出去，这样不容易被外在用户所察觉！达到隐藏自己的目的！

6，SRV记录 SRV记录是服务器资源记录的缩写，SRV记录是DNS记录中的新鲜面孔，在RFC2052中才对SRV记录进行了定义，因此很多老版本的DNS服务器并不支持SRV记录。那么SRV记录有什么用呢？SRV记录的作用是说明一个服务器能够提供什么样的服务！SRV记录在微软的Active Directory中有着重要地位，大家知道在NT4时代域和DNS并没有太多关系。但从Win2000开始，域就离不开DNS的帮助了，为什么呢？因为域内的计算机要依赖DNS的SRV记录来定位域控制器！表现形式为：—ldap._tcp.contoso.com 600 IN SRV 0 100 389 NS.contoso.com ladp: 是一个服务，该标识说明把这台服务器当做响应LDAP请求的服务器 tcp：本服务使用的协议，可以是tcp，也可以是用户数据包协议《udp》 contoso.com：此记录所值的域名 600： 此记录默认生存时间（秒） IN： 标准DNS Internet类 SRV：将这条记录标识为SRV记录 0： 优先级，如果相同的服务有多条SRV记录，用户会尝试先连接优先级最低的记录 100：负载平衡机制，多条SRV并且优先级也相同，那么用户会先尝试连接权重高的记录 389：此服务使用的端口 NS.contoso.com:提供此服务的主机

7,PTR记录 PTR记录也被称为指针记录，PTR记录是A记录的逆向记录，作用是把IP地址解析为域名。由于我们在前面提到过，DNS的反向区域负责从IP到域名的解析，因此如果要创建PTR记录，必须在反向区域中创建。

SOA记录和NS记录的通俗解释 DNS服务器里有两个比较重要的记录。一个叫SOA记录（起始授权机构） 一个叫NS（Name Server）记录（域名服务器） SOA记录表明了DNS服务器之间的关系。SOA记录表明了谁是这个区域的所有者。比如51CTO.COM这个区域。一个DNS服务器安装后，需要创建一个区域，以后这个区域的查询解析，都是通过DNS服务器来完成的。现在来说一下所有者，我这里所说的所有者，就是谁对这个区域有修改权利。常见的DNS服务器只能创建一个标准区域，然后可以创建很多个辅助区域。标准区域是可以读写修改的。而辅助区域只能通过标准区域复制来完成，不能在辅助区域中进行修改。而创建标准区域的DNS就会有SOA记录，或者准确说SOA记录中的主机地址一定是这个标准区域的服务器IP地址

---------------------------
服务器的包含关系是： 根DNS服务器  > 顶级DNS服务器 > 权威DNS服务器 > 二级域名的DNS服务器
根DNS服务器　　：掌握着所有顶级dns的ip和域名的对应关系
顶级DNS服务器　：掌握着权威dns的ip和域名的对应关系
权威DNS服务器　：掌握着二级域名dns服务器的ip和域名的对应关系