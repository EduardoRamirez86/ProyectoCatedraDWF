import SecureLS from 'secure-ls';

const secureLs = new SecureLS({
  encodingType: 'aes',
  isCompression: true,
  encryptionSecret: 'miClaveSecreta123'
});

export const secureSetItem = (key, value) => {
  secureLs.set(key, value);
};

export const secureGetItem = (key) => {
  return secureLs.get(key);
};

export const secureRemoveItem = (key) => {
  secureLs.remove(key);
};