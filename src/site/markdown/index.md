# Introduction

This module implements a generic monitoring facility for any Enterprise Java Application.

# What is Monitoring?

In the past, ETECTURE made client-server applications and doesn't care what the applications current state is. Instead, the customer informed us about a non running application and does a manual proof of live.

Monitoring means, that an application provides informations about it's current state to an operator.
These informations basically has statistic characteristics and are called KPI (key performance indicator). Several KPI's are provided out-of-the-box with this module, while an application can also provide it's own KPI's that are collected by this module.

# Default KPI's

The KPI implementations provided by this module are:

**Standard KPI's**

| KPI-Name		| Description 										          |
| :------------ | :---------------------------------------------------------- |
| Uptime        | duration since last Server-Start or Deployment              |
| Version       | Die installierte Version des Moduls                         |

**Request-based KPI's**

| KPI-Name		| Description 										          |
| :------------ | :---------------------------------------------------------- |
| requestCount	| count of requests made to the service						  |
| failureCount  | count of requests made to the service that raises a failure |
| MTBF			| Mean-Time-Between-Failure							          |
| FPM           | Failure per Minute                                          |
| FPR           | Failure per Request = (failureCount / requestCount) * 100%  |
| RPM           | count of requsts per Minute                                 |
| ART			| Average Request Time										  |
| SRT			| Shortest Request Time										  |
| LRT			| Longest Request Time										  | 
| CILH          | count of requests made in the last hour                     |

**Aggregating KPI's**

| KPI-Name		| Description 										          |
| :------------ | :---------------------------------------------------------- |
| Health-Status | applications current status of health						  |

