# kube-badger chart

This chart bootstraps the [`kube-badger`](https://github.com/tobilarscheid/kube-badger) on a kubernetes cluster.

There is two important things to configure when running the chart:

## RBAC

The chart creates a `serviceaccount`, a `clusterrole` and a `clusterrolebinding` of the account and the role. The default `clusterrole` provides read access to `pods` and `deployments`. To override this, create `rbac.clusterrole.rules` section in your values.yaml and add or remove the respective rules:

```yaml
rbac:
  clusterrole:
    rules:
      - apiGroups:
          - ""
        resources:
          - pods
        verbs:
          - get
    - ADD YOUR RULES HERE
```

## Ingress rules

Because of the way you pass API resources to the kube-badger, you can easily restrict access to only certain resources by narrowing down the Ingress Rules. By default, all paths under `/badge` are routed to the badger. You can add more specific paths to narrow down what is reachable:

```yaml
ingress:
  paths: 
    - "/badge/apis/extensions/v1beta1/namespaces/default/deployments/petstore"
```

Also, make sure you have configured the hosts you want the kube-badger to be reachable under:

```yaml
ingress:
    hosts:
     - your-cluster.com
```