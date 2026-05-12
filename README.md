# MvvmSample
 A simple example of MVVM
<img width="1200" height="2640" alt="3925d9fbfdb69c1bcf080e59f24ba5ee" src="https://github.com/user-attachments/assets/2440b225-ca13-4872-a5bc-cf6b471d1177" />
<img width="1200" height="2640" alt="c525ab0e88dd2a8eb1c9d0c3dae11eb1" src="https://github.com/user-attachments/assets/e022ee1f-9f58-4137-aa4b-f7b4cf748ecf" />
## 功能说明（客户端项目）

### 1. 顶部按钮区
- 最上方一行共有 **三个按钮**：
  - **第一个按钮（扳手图标）**：用于打开“服务端配置弹窗”，可修改服务端的 IP 与端口。
  - **第二个按钮（全选）**：选中当前列表中的所有图片。
  - **第三个按钮（下载到相册）**：将已选中的所有图片保存到本地相册。

---

### 2. 服务端配置弹窗
- 弹窗中包含两个输入框：
  - **IP 输入框**：填写服务端（MacFolderService）显示的 IP。
  - **端口输入框**：填写服务端显示的端口号。
- ⚠️ **注意：IP 与端口必须与 MacFolderService 项目中显示的完全一致，否则无法访问图片。**
- 弹窗底部有两个按钮：
  - **取消**：关闭弹窗，不保存任何修改。
  - **保存**：保存修改后的 IP 与端口配置。

---
